# Chatwoot Stress Testing Script
# Runs comprehensive stress tests to validate system performance under extreme load

param(
    [Parameter(Mandatory=$false)]
    [string]$BaseUrl = "http://localhost:8080",
    
    [Parameter(Mandatory=$false)]
    [int]$Duration = 300,
    
    [Parameter(Mandatory=$false)]
    [int]$ConcurrentUsers = 100,
    
    [Parameter(Mandatory=$false)]
    [string]$TestType = "all",
    
    [Parameter(Mandatory=$false)]
    [switch]$Continuous,
    
    [Parameter(Mandatory=$false)]
    [switch]$GenerateReport
)

# Configuration
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$ProjectRoot = Split-Path -Parent $ScriptDir
$LogFile = Join-Path $ProjectRoot "stress-test.log"
$ReportDir = Join-Path $ProjectRoot "test-reports"

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

function Test-Prerequisites {
    Write-Step "Testing Prerequisites"
    
    # Check if Java is available
    try {
        $javaVersion = java -version 2>&1
        if ($javaVersion) {
            Write-Success "Java found: $($javaVersion[0])"
        } else {
            throw "Java not found"
        }
    } catch {
        Write-Error "Java not found. Please ensure Java 17+ is installed and in PATH"
        exit 1
    }
    
    # Check if Maven is available
    try {
        $mavenVersion = mvn --version 2>$null
        if ($mavenVersion) {
            Write-Success "Maven found: $($mavenVersion[0])"
        } else {
            throw "Maven not found"
        }
    } catch {
        Write-Error "Maven not found. Please ensure Maven is installed and in PATH"
        exit 1
    }
    
    # Check if application is running
    try {
        $response = Invoke-RestMethod -Uri "$BaseUrl/actuator/health" -Method Get -TimeoutSec 10
        if ($response.status -eq "UP") {
            Write-Success "Application is running and healthy"
        } else {
            throw "Application health check failed: $($response.status)"
        }
    } catch {
        Write-Error "Application is not accessible at $BaseUrl"
        Write-Info "Please ensure the Chatwoot backend is running"
        exit 1
    }
    
    Write-Success "All prerequisites met"
}

function Run-GatlingTests {
    Write-Step "Running Gatling Load Tests"
    
    Push-Location $ProjectRoot
    
    try {
        # Create test reports directory
        if (-not (Test-Path $ReportDir)) {
            New-Item -ItemType Directory -Path $ReportDir -Force | Out-Null
        }
        
        Write-Info "Starting Gatling load tests..."
        Write-Info "Duration: $Duration seconds"
        Write-Info "Concurrent Users: $ConcurrentUsers"
        Write-Info "Base URL: $BaseUrl"
        
        # Run Gatling tests
        mvn gatling:test -Dgatling.simulationClass=com.weavecode.chatwoot.testing.LoadTestingSimulation
        
        if ($LASTEXITCODE -eq 0) {
            Write-Success "Gatling tests completed successfully"
        } else {
            throw "Gatling tests failed with exit code $LASTEXITCODE"
        }
        
    } catch {
        Write-Error "Gatling tests failed: $_"
        exit 1
    } finally {
        Pop-Location
    }
}

function Run-HealthCheckStress {
    Write-Step "Running Health Check Stress Test"
    
    $startTime = Get-Date
    $endTime = $startTime.AddSeconds($Duration)
    $requestCount = 0
    $successCount = 0
    $errorCount = 0
    $totalResponseTime = 0
    
    Write-Info "Starting health check stress test for $Duration seconds..."
    
    while ((Get-Date) -lt $endTime) {
        $requestCount++
        $requestStart = Get-Date
        
        try {
            $response = Invoke-RestMethod -Uri "$BaseUrl/actuator/health" -Method Get -TimeoutSec 30
            $successCount++
            
            if ($response.status -eq "UP") {
                $responseTime = ((Get-Date) - $requestStart).TotalMilliseconds
                $totalResponseTime += $responseTime
                
                if ($responseTime -gt 1000) {
                    Write-Warning "Slow response: ${responseTime}ms"
                }
            } else {
                $errorCount++
                Write-Error "Health check returned: $($response.status)"
            }
        } catch {
            $errorCount++
            Write-Error "Health check failed: $_"
        }
        
        # Small delay between requests
        Start-Sleep -Milliseconds 100
    }
    
    $avgResponseTime = if ($successCount -gt 0) { $totalResponseTime / $successCount } else { 0 }
    $successRate = if ($requestCount -gt 0) { ($successCount / $requestCount) * 100 } else { 0 }
    
    Write-Success "Health Check Stress Test Results:"
    Write-Info "Total Requests: $requestCount"
    Write-Info "Successful: $successCount"
    Write-Info "Errors: $errorCount"
    Write-Info "Success Rate: $([math]::Round($successRate, 2))%"
    Write-Info "Average Response Time: $([math]::Round($avgResponseTime, 2))ms"
}

function Run-AuthenticationStress {
    Write-Step "Running Authentication Stress Test"
    
    $startTime = Get-Date
    $endTime = $startTime.AddSeconds($Duration)
    $requestCount = 0
    $successCount = 0
    $errorCount = 0
    $totalResponseTime = 0
    
    Write-Info "Starting authentication stress test for $Duration seconds..."
    
    # Test data
    $testUsers = @(
        @{email = "test.user1@weavecode.co.uk"; password = "password123"},
        @{email = "test.user2@weavecode.co.uk"; password = "password123"},
        @{email = "test.user3@weavecode.co.uk"; password = "password123"},
        @{email = "test.user4@weavecode.co.uk"; password = "password123"},
        @{email = "test.user5@weavecode.co.uk"; password = "password123"}
    )
    
    while ((Get-Date) -lt $endTime) {
        $requestCount++
        $requestStart = Get-Date
        $testUser = $testUsers | Get-Random
        
        try {
            $body = @{
                email = $testUser.email
                password = $testUser.password
            } | ConvertTo-Json
            
            $response = Invoke-RestMethod -Uri "$BaseUrl/api/auth/login" -Method Post -Body $body -ContentType "application/json" -TimeoutSec 30
            
            if ($response.token) {
                $successCount++
                $responseTime = ((Get-Date) - $requestStart).TotalMilliseconds
                $totalResponseTime += $responseTime
                
                if ($responseTime -gt 2000) {
                    Write-Warning "Slow authentication: ${responseTime}ms"
                }
            } else {
                $errorCount++
                Write-Error "Authentication failed: No token received"
            }
        } catch {
            $errorCount++
            Write-Error "Authentication failed: $_"
        }
        
        # Small delay between requests
        Start-Sleep -Milliseconds 200
    }
    
    $avgResponseTime = if ($successCount -gt 0) { $totalResponseTime / $successCount } else { 0 }
    $successRate = if ($requestCount -gt 0) { ($successCount / $requestCount) * 100 } else { 0 }
    
    Write-Success "Authentication Stress Test Results:"
    Write-Info "Total Requests: $requestCount"
    Write-Info "Successful: $successCount"
    Write-Info "Errors: $errorCount"
    Write-Info "Success Rate: $([math]::Round($successRate, 2))%"
    Write-Info "Average Response Time: $([math]::Round($avgResponseTime, 2))ms"
}

function Run-MetricsStress {
    Write-Step "Running Metrics Endpoints Stress Test"
    
    $startTime = Get-Date
    $endTime = $startTime.AddSeconds($Duration)
    $requestCount = 0
    $successCount = 0
    $errorCount = 0
    $totalResponseTime = 0
    
    Write-Info "Starting metrics stress test for $Duration seconds..."
    
    $endpoints = @(
        "/actuator/health",
        "/actuator/metrics",
        "/actuator/prometheus",
        "/actuator/info"
    )
    
    while ((Get-Date) -lt $endTime) {
        $requestCount++
        $requestStart = Get-Date
        $endpoint = $endpoints | Get-Random
        
        try {
            $response = Invoke-RestMethod -Uri "$BaseUrl$endpoint" -Method Get -TimeoutSec 30
            
            if ($response) {
                $successCount++
                $responseTime = ((Get-Date) - $requestStart).TotalMilliseconds
                $totalResponseTime += $responseTime
                
                if ($responseTime -gt 1500) {
                    Write-Warning "Slow metrics response: ${responseTime}ms for $endpoint"
                }
            } else {
                $errorCount++
                Write-Error "Metrics endpoint failed: Empty response for $endpoint"
            }
        } catch {
            $errorCount++
            Write-Error "Metrics endpoint failed: $_ for $endpoint"
        }
        
        # Small delay between requests
        Start-Sleep -Milliseconds 150
    }
    
    $avgResponseTime = if ($successCount -gt 0) { $totalResponseTime / $successCount } else { 0 }
    $successRate = if ($requestCount -gt 0) { ($successCount / $requestCount) * 100 } else { 0 }
    
    Write-Success "Metrics Stress Test Results:"
    Write-Info "Total Requests: $requestCount"
    Write-Info "Successful: $successCount"
    Write-Info "Errors: $errorCount"
    Write-Info "Success Rate: $([math]::Round($successRate, 2))%"
    Write-Info "Average Response Time: $([math]::Round($avgResponseTime, 2))ms"
}

function Generate-TestReport {
    Write-Step "Generating Test Report"
    
    try {
        $reportFile = Join-Path $ReportDir "stress-test-report-$(Get-Date -Format 'yyyy-MM-dd-HHmmss').html"
        
        $htmlContent = @"
<!DOCTYPE html>
<html>
<head>
    <title>Chatwoot Stress Test Report</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .header { background-color: #f0f0f0; padding: 20px; border-radius: 5px; }
        .summary { background-color: #e8f5e8; padding: 15px; border-radius: 5px; margin: 20px 0; }
        .test-results { margin: 20px 0; }
        .test-item { background-color: #f9f9f9; padding: 10px; margin: 10px 0; border-radius: 3px; }
        .success { color: green; }
        .error { color: red; }
        .warning { color: orange; }
    </style>
</head>
<body>
    <div class="header">
        <h1>🚀 Chatwoot Backend Stress Test Report</h1>
        <p><strong>Generated:</strong> $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')</p>
        <p><strong>Base URL:</strong> $BaseUrl</p>
        <p><strong>Test Duration:</strong> $Duration seconds</p>
        <p><strong>Concurrent Users:</strong> $ConcurrentUsers</p>
    </div>
    
    <div class="summary">
        <h2>📊 Test Summary</h2>
        <p>This report contains the results of comprehensive stress testing performed on the Chatwoot backend.</p>
        <p>Tests included: Health Checks, Authentication, Metrics Endpoints, and Gatling Load Tests.</p>
    </div>
    
    <div class="test-results">
        <h2>🧪 Test Results</h2>
        <div class="test-item">
            <h3>Health Check Stress Test</h3>
            <p>Endpoint: /actuator/health</p>
            <p>Duration: $Duration seconds</p>
        </div>
        
        <div class="test-item">
            <h3>Authentication Stress Test</h3>
            <p>Endpoint: /api/auth/login</p>
            <p>Duration: $Duration seconds</p>
        </div>
        
        <div class="test-item">
            <h3>Metrics Endpoints Stress Test</h3>
            <p>Endpoints: /actuator/health, /actuator/metrics, /actuator/prometheus, /actuator/info</p>
            <p>Duration: $Duration seconds</p>
        </div>
        
        <div class="test-item">
            <h3>Gatling Load Tests</h3>
            <p>Comprehensive load testing with multiple scenarios</p>
            <p>Duration: $Duration seconds</p>
        </div>
    </div>
    
    <div class="summary">
        <h2>📈 Performance Insights</h2>
        <p>• Response times should remain under 1 second for 95% of requests</p>
        <p>• Success rate should be above 95% under normal load</p>
        <p>• System should handle $ConcurrentUsers concurrent users gracefully</p>
        <p>• Health checks should respond within 500ms</p>
    </div>
</body>
</html>
"@
        
        $htmlContent | Out-File -FilePath $reportFile -Encoding UTF8
        Write-Success "Test report generated: $reportFile"
        
    } catch {
        Write-Error "Failed to generate test report: $_"
    }
}

function Show-TestSummary {
    Write-Header "Stress Testing Summary"
    
    Write-Info "Test Configuration:"
    Write-Info "  Base URL: $BaseUrl"
    Write-Info "  Duration: $Duration seconds"
    Write-Info "  Concurrent Users: $ConcurrentUsers"
    Write-Info "  Test Type: $TestType"
    Write-Info "  Continuous Mode: $Continuous"
    Write-Info "  Generate Report: $GenerateReport"
    
    Write-ColorOutput "`n📋 Available Test Types:" $Blue
    Write-ColorOutput "  all - Run all stress tests" $Green
    Write-ColorOutput "  health - Health check stress test only" $Green
    Write-ColorOutput "  auth - Authentication stress test only" $Green
    Write-ColorOutput "  metrics - Metrics endpoints stress test only" $Green
    Write-ColorOutput "  gatling - Gatling load tests only" $Green
    
    Write-ColorOutput "`n🚀 Next Steps:" $Blue
    Write-ColorOutput "1. Review test results in logs: $LogFile" $Green
    Write-ColorOutput "2. Check test reports in: $ReportDir" $Green
    Write-ColorOutput "3. Analyze performance metrics" $Green
    Write-ColorOutput "4. Optimize based on findings" $Green
}

# Main execution
try {
    Write-Header "Chatwoot Backend Stress Testing"
    Write-Info "Base URL: $BaseUrl"
    Write-Info "Duration: $Duration seconds"
    Write-Info "Concurrent Users: $ConcurrentUsers"
    Write-Info "Test Type: $TestType"
    Write-Info "Log file: $LogFile"
    
    # Clear log file
    if (Test-Path $LogFile) {
        Remove-Item $LogFile -Force
    }
    
    # Test prerequisites
    Test-Prerequisites
    
    # Run tests based on type
    switch ($TestType.ToLower()) {
        "all" {
            Run-HealthCheckStress
            Run-AuthenticationStress
            Run-MetricsStress
            Run-GatlingTests
        }
        "health" {
            Run-HealthCheckStress
        }
        "auth" {
            Run-AuthenticationStress
        }
        "metrics" {
            Run-MetricsStress
        }
        "gatling" {
            Run-GatlingTests
        }
        default {
            Write-Error "Invalid test type: $TestType"
            Write-Info "Valid types: all, health, auth, metrics, gatling"
            exit 1
        }
    }
    
    # Generate report if requested
    if ($GenerateReport) {
        Generate-TestReport
    }
    
    # Show summary
    Show-TestSummary
    
    Write-Header "Stress Testing Completed Successfully"
    
} catch {
    Write-Error "Stress testing failed: $_"
    exit 1
}
