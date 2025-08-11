# Chatwoot Railway Deployment Monitor
# Monitors deployment status and health checks

param(
    [Parameter(Mandatory=$false)]
    [string]$RailwayProject = "chatwoot-weavecode",
    
    [Parameter(Mandatory=$false)]
    [int]$Interval = 30,
    
    [Parameter(Mandatory=$false)]
    [int]$Timeout = 1800,
    
    [Parameter(Mandatory=$false)]
    [switch]$Continuous
)

# Configuration
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$ProjectRoot = Split-Path -Parent $ScriptDir
$LogFile = Join-Path $ProjectRoot "monitoring.log"

# Colors for output
$Green = "Green"
$Yellow = "Yellow"
$Red = "Red"
$Blue = "Blue"

function Write-ColorOutput {
    param(
        [string]$Message,
        [string]$Color = "White"
    )
    Write-Host $Message -ForegroundColor $Color
    Add-Content -Path $LogFile -Value "$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss'): $Message"
}

function Write-Header {
    param([string]$Title)
    Write-ColorOutput "`n" $Blue
    Write-ColorOutput "=" * 80 $Blue
    Write-ColorOutput " $Title" $Blue
    Write-ColorOutput "=" * 80 $Blue
    Write-ColorOutput "`n" $Blue
}

function Write-Step {
    param([string]$Step)
    Write-ColorOutput "`n▶ $Step" $Yellow
}

function Write-Success {
    param([string]$Message)
    Write-ColorOutput "✅ $Message" $Green
}

function Write-Error {
    param([string]$Message)
    Write-ColorOutput "❌ $Message" $Red
}

function Write-Info {
    param([string]$Message)
    Write-ColorOutput "ℹ️ $Message" $Blue
}

function Write-Warning {
    param([string]$Message)
    Write-ColorOutput "⚠️ $Message" $Yellow
}

function Test-RailwayCLI {
    try {
        $railwayVersion = railway --version 2>$null
        return $railwayVersion -ne $null
    } catch {
        return $false
    }
}

function Test-RailwayAuth {
    try {
        $loginStatus = railway whoami 2>$null
        return $loginStatus -and -not ($loginStatus -like "*not logged in*")
    } catch {
        return $false
    }
}

function Get-DeploymentStatus {
    try {
        $status = railway status --json | ConvertFrom-Json
        return $status
    } catch {
        Write-Error "Failed to get deployment status: $_"
        return $null
    }
}

function Show-DeploymentInfo {
    param([object]$Status)
    
    if (-not $Status) {
        Write-Error "No deployment status available"
        return
    }
    
    Write-ColorOutput "`n📊 Deployment Status:" $Blue
    Write-ColorOutput "Project: $($Status.name)" $Green
    Write-ColorOutput "Last Updated: $($Status.updatedAt)" $Green
    
    if ($Status.deployments -and $Status.deployments.Count -gt 0) {
        $latestDeployment = $Status.deployments[0]
        Write-ColorOutput "Latest Deployment:" $Blue
        Write-ColorOutput "  Status: $($latestDeployment.status)" $(if ($latestDeployment.status -eq "SUCCESS") { $Green } else { $Red })
        Write-ColorOutput "  Created: $($latestDeployment.createdAt)" $Green
        Write-ColorOutput "  Updated: $($latestDeployment.updatedAt)" $Green
        
        if ($latestDeployment.status -eq "FAILED") {
            Write-ColorOutput "  Error: $($latestDeployment.error)" $Red
        }
    }
    
    if ($Status.services -and $Status.services.Count -gt 0) {
        Write-ColorOutput "`n🔧 Services:" $Blue
        foreach ($service in $Status.services) {
            $statusColor = switch ($service.status) {
                "RUNNING" { $Green }
                "DEPLOYING" { $Yellow }
                "FAILED" { $Red }
                default { $Yellow }
            }
            
            Write-ColorOutput "  $($service.name): $($service.status)" $statusColor
            if ($service.url) {
                Write-ColorOutput "    URL: $($service.url)" $Green
            }
        }
    }
}

function Test-HealthEndpoint {
    param([string]$BaseUrl)
    
    if (-not $BaseUrl) {
        Write-Warning "No service URL available for health check"
        return $false
    }
    
    $healthEndpoint = "$BaseUrl/actuator/health"
    
    try {
        Write-Info "Testing health endpoint: $healthEndpoint"
        $response = Invoke-RestMethod -Uri $healthEndpoint -Method Get -TimeoutSec 30
        
        if ($response.status -eq "UP") {
            Write-Success "Health check passed: $($response.status)"
            
            # Show component health
            if ($response.components) {
                Write-ColorOutput "  Component Health:" $Blue
                foreach ($component in $response.components.PSObject.Properties) {
                    $componentStatus = $component.Value.status
                    $statusColor = if ($componentStatus -eq "UP") { $Green } else { $Red }
                    Write-ColorOutput "    $($component.Name): $componentStatus" $statusColor
                }
            }
            
            return $true
        } else {
            Write-Error "Health check failed: $($response.status)"
            return $false
        }
        
    } catch {
        Write-Error "Health check request failed: $_"
        return $false
    }
}

function Test-MetricsEndpoint {
    param([string]$BaseUrl)
    
    if (-not $BaseUrl) {
        return $false
    }
    
    $metricsEndpoint = "$BaseUrl/actuator/metrics"
    
    try {
        Write-Info "Testing metrics endpoint: $metricsEndpoint"
        $response = Invoke-RestMethod -Uri $metricsEndpoint -Method Get -TimeoutSec 30
        
        if ($response.names) {
            Write-Success "Metrics endpoint accessible: $($response.names.Count) metrics available"
            return $true
        } else {
            Write-Warning "Metrics endpoint returned no metrics"
            return $false
        }
        
    } catch {
        Write-Warning "Metrics endpoint test failed: $_"
        return $false
    }
}

function Test-PrometheusEndpoint {
    param([string]$BaseUrl)
    
    if (-not $BaseUrl) {
        return $false
    }
    
    $prometheusEndpoint = "$BaseUrl/actuator/prometheus"
    
    try {
        Write-Info "Testing Prometheus endpoint: $prometheusEndpoint"
        $response = Invoke-RestMethod -Uri $prometheusEndpoint -Method Get -TimeoutSec 30
        
        if ($response -and $response.Length -gt 0) {
            Write-Success "Prometheus endpoint accessible: $($response.Length) characters of metrics data"
            return $true
        } else {
            Write-Warning "Prometheus endpoint returned no data"
            return $false
        }
        
    } catch {
        Write-Warning "Prometheus endpoint test failed: $_"
        return $false
    }
}

function Monitor-Deployment {
    param([int]$MaxAttempts)
    
    $attempt = 0
    $startTime = Get-Date
    
    while ($attempt -lt $MaxAttempts) {
        $attempt++
        $elapsedTime = (Get-Date) - $startTime
        
        Write-Header "Monitoring Attempt $attempt/$MaxAttempts (Elapsed: $($elapsedTime.ToString('mm\:ss'))"
        
        # Get deployment status
        $status = Get-DeploymentStatus
        if ($status) {
            Show-DeploymentInfo -Status $status
            
            # Test health endpoints if service is running
            $runningService = $status.services | Where-Object { $_.status -eq "RUNNING" -and $_.url }
            if ($runningService) {
                $serviceUrl = $runningService[0].url
                
                Write-Step "Testing Service Endpoints"
                Test-HealthEndpoint -BaseUrl $serviceUrl
                Test-MetricsEndpoint -BaseUrl $serviceUrl
                Test-PrometheusEndpoint -BaseUrl $serviceUrl
            }
        }
        
        # Check if deployment is complete
        if ($status.deployments -and $status.deployments[0].status -eq "SUCCESS") {
            Write-Success "Deployment completed successfully!"
            return $true
        } elseif ($status.deployments -and $status.deployments[0].status -eq "FAILED") {
            Write-Error "Deployment failed!"
            return $false
        }
        
        if (-not $Continuous -or $attempt -ge $MaxAttempts) {
            break
        }
        
        Write-Info "Waiting $Interval seconds before next check..."
        Start-Sleep -Seconds $Interval
    }
    
    if ($attempt -ge $MaxAttempts) {
        Write-Error "Monitoring timeout reached"
        return $false
    }
    
    return $true
}

function Show-MonitoringSummary {
    Write-Header "Monitoring Summary"
    
    Write-Info "Project: $RailwayProject"
    Write-Info "Check Interval: $Interval seconds"
    Write-Info "Timeout: $Timeout seconds"
    Write-Info "Continuous Mode: $Continuous"
    
    Write-ColorOutput "`n📋 Available Commands:" $Blue
    Write-ColorOutput "  .\scripts\monitor-deployment.ps1 -Continuous" $Green
    Write-ColorOutput "  .\scripts\monitor-deployment.ps1 -Interval 60" $Green
    Write-ColorOutput "  .\scripts\monitor-deployment.ps1 -Timeout 3600" $Green
    
    Write-ColorOutput "`n🔍 Monitoring Dashboard:" $Blue
    Write-ColorOutput "  Railway Dashboard: https://railway.app/project/$RailwayProject" $Green
    Write-ColorOutput "  Logs: $LogFile" $Green
}

# Main execution
try {
    Write-Header "Chatwoot Railway Deployment Monitor"
    Write-Info "Project: $RailwayProject"
    Write-Info "Check Interval: $Interval seconds"
    Write-Info "Timeout: $Timeout seconds"
    Write-Info "Log file: $LogFile"
    
    # Clear log file
    if (Test-Path $LogFile) {
        Remove-Item $LogFile -Force
    }
    
    # Test prerequisites
    if (-not (Test-RailwayCLI)) {
        Write-Error "Railway CLI not found. Please install it first: npm install -g @railway/cli"
        exit 1
    }
    
    if (-not (Test-RailwayAuth)) {
        Write-Error "Not authenticated with Railway. Please run 'railway login' first"
        exit 1
    }
    
    # Calculate max attempts
    $maxAttempts = [math]::Ceiling($Timeout / $Interval)
    
    Write-Info "Maximum monitoring attempts: $maxAttempts"
    
    # Start monitoring
    $success = Monitor-Deployment -MaxAttempts $maxAttempts
    
    if ($success) {
        Write-Header "Monitoring Completed Successfully"
        Show-MonitoringSummary
    } else {
        Write-Header "Monitoring Completed with Issues"
        Write-Error "Some issues were detected during monitoring"
        Show-MonitoringSummary
        exit 1
    }
    
} catch {
    Write-Error "Monitoring failed: $_"
    exit 1
}
