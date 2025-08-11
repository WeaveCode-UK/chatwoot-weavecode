# Chatwoot Railway Deployment Script
# Automated deployment script for Chatwoot multi-tenant backend

param(
    [Parameter(Mandatory=$false)]
    [string]$Environment = "production",
    
    [Parameter(Mandatory=$false)]
    [string]$RailwayProject = "chatwoot-weavecode",
    
    [Parameter(Mandatory=$false)]
    [switch]$SkipBuild,
    
    [Parameter(Mandatory=$false)]
    [switch]$SkipTests,
    
    [Parameter(Mandatory=$false)]
    [switch]$Force
)

# Configuration
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$ProjectRoot = Split-Path -Parent $ScriptDir
$LogFile = Join-Path $ProjectRoot "deployment.log"

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

function Test-Prerequisites {
    Write-Step "Testing Prerequisites"
    
    # Check if Railway CLI is installed
    try {
        $railwayVersion = railway --version 2>$null
        if ($railwayVersion) {
            Write-Success "Railway CLI found: $railwayVersion"
        } else {
            throw "Railway CLI not found"
        }
    } catch {
        Write-Error "Railway CLI not installed. Please install it first:"
        Write-Info "npm install -g @railway/cli"
        Write-Info "railway login"
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
    
    Write-Success "All prerequisites met"
}

function Build-Project {
    Write-Step "Building Chatwoot Backend"
    
    if ($SkipBuild) {
        Write-Info "Skipping build as requested"
        return
    }
    
    Push-Location $ProjectRoot
    
    try {
        Write-Info "Cleaning previous build..."
        mvn clean
        
        Write-Info "Building project..."
        mvn package -DskipTests
        
        if ($LASTEXITCODE -eq 0) {
            Write-Success "Project built successfully"
        } else {
            throw "Build failed with exit code $LASTEXITCODE"
        }
    } catch {
        Write-Error "Build failed: $_"
        exit 1
    } finally {
        Pop-Location
    }
}

function Run-Tests {
    Write-Step "Running Tests"
    
    if ($SkipTests) {
        Write-Info "Skipping tests as requested"
        return
    }
    
    Push-Location $ProjectRoot
    
    try {
        Write-Info "Running unit tests..."
        mvn test
        
        if ($LASTEXITCODE -eq 0) {
            Write-Success "All tests passed"
        } else {
            throw "Tests failed with exit code $LASTEXITCODE"
        }
    } catch {
        Write-Error "Tests failed: $_"
        if (-not $Force) {
            exit 1
        } else {
            Write-Info "Continuing due to --Force flag"
        }
    } finally {
        Pop-Location
    }
}

function Deploy-Railway {
    Write-Step "Deploying to Railway"
    
    Push-Location $ProjectRoot
    
    try {
        # Check if we're logged into Railway
        Write-Info "Checking Railway authentication..."
        $loginStatus = railway whoami 2>$null
        
        if (-not $loginStatus -or $loginStatus -like "*not logged in*") {
            Write-Error "Not logged into Railway. Please run 'railway login' first"
            exit 1
        }
        
        Write-Success "Authenticated as: $loginStatus"
        
        # Link project if not already linked
        Write-Info "Linking Railway project..."
        railway link --project $RailwayProject
        
        # Set environment variables
        Write-Info "Setting environment variables..."
        Set-RailwayEnvironment
        
        # Deploy
        Write-Info "Deploying to Railway..."
        railway up --detach
        
        if ($LASTEXITCODE -eq 0) {
            Write-Success "Deployment initiated successfully"
        } else {
            throw "Deployment failed with exit code $LASTEXITCODE"
        }
        
        # Wait for deployment to complete
        Wait-ForDeployment
        
    } catch {
        Write-Error "Deployment failed: $_"
        exit 1
    } finally {
        Pop-Location
    }
}

function Set-RailwayEnvironment {
    Write-Info "Configuring environment variables..."
    
    # Database configuration
    railway variables set DATABASE_URL="$env:DATABASE_URL" --service chatwoot-backend
    railway variables set DATABASE_USERNAME="$env:DATABASE_USERNAME" --service chatwoot-backend
    railway variables set DATABASE_PASSWORD="$env:DATABASE_PASSWORD" --service chatwoot-backend
    
    # Redis configuration
    railway variables set REDIS_HOST="$env:REDIS_HOST" --service chatwoot-backend
    railway variables set REDIS_PORT="$env:REDIS_PORT" --service chatwoot-backend
    railway variables set REDIS_PASSWORD="$env:REDIS_PASSWORD" --service chatwoot-backend
    
    # JWT configuration
    railway variables set JWT_SECRET="$env:JWT_SECRET" --service chatwoot-backend
    railway variables set JWT_EXPIRATION="$env:JWT_EXPIRATION" --service chatwoot-backend
    
    # API keys
    railway variables set OPENAI_API_KEY="$env:OPENAI_API_KEY" --service chatwoot-backend
    railway variables set STRIPE_API_KEY="$env:STRIPE_API_KEY" --service chatwoot-backend
    railway variables set PAYPAL_CLIENT_ID="$env:PAYPAL_CLIENT_ID" --service chatwoot-backend
    railway variables set PAYPAL_CLIENT_SECRET="$env:PAYPAL_CLIENT_SECRET" --service chatwoot-backend
    railway variables set SENDGRID_API_KEY="$env:SENDGRID_API_KEY" --service chatwoot-backend
    
    # Environment
    railway variables set SPRING_PROFILES_ACTIVE="$Environment" --service chatwoot-backend
    railway variables set PORT="8080" --service chatwoot-backend
    
    Write-Success "Environment variables configured"
}

function Wait-ForDeployment {
    Write-Step "Waiting for Deployment to Complete"
    
    $maxAttempts = 30
    $attempt = 0
    
    while ($attempt -lt $maxAttempts) {
        $attempt++
        Write-Info "Checking deployment status (attempt $attempt/$maxAttempts)..."
        
        try {
            $status = railway status --json | ConvertFrom-Json
            $deploymentStatus = $status.deployments[0].status
            
            switch ($deploymentStatus) {
                "SUCCESS" {
                    Write-Success "Deployment completed successfully!"
                    return
                }
                "FAILED" {
                    throw "Deployment failed"
                }
                "IN_PROGRESS" {
                    Write-Info "Deployment in progress... waiting 10 seconds"
                    Start-Sleep -Seconds 10
                }
                default {
                    Write-Info "Deployment status: $deploymentStatus... waiting 10 seconds"
                    Start-Sleep -Seconds 10
                }
            }
        } catch {
            Write-Info "Waiting for deployment to start... (attempt $attempt/$maxAttempts)"
            Start-Sleep -Seconds 10
        }
    }
    
    Write-Error "Deployment timeout after $maxAttempts attempts"
    exit 1
}

function Run-DatabaseMigrations {
    Write-Step "Running Database Migrations"
    
    try {
        Write-Info "Running Flyway migrations..."
        railway run -- mvn flyway:migrate
        
        if ($LASTEXITCODE -eq 0) {
            Write-Success "Database migrations completed successfully"
        } else {
            throw "Database migrations failed with exit code $LASTEXITCODE"
        }
    } catch {
        Write-Error "Database migrations failed: $_"
        exit 1
    }
}

function Test-HealthChecks {
    Write-Step "Testing Health Checks"
    
    try {
        Write-Info "Waiting for application to start..."
        Start-Sleep -Seconds 30
        
        Write-Info "Testing health endpoint..."
        $healthUrl = railway status --json | ConvertFrom-Json | Select-Object -ExpandProperty services | Where-Object { $_.name -eq "chatwoot-backend" } | Select-Object -ExpandProperty url
        
        if (-not $healthUrl) {
            throw "Could not determine service URL"
        }
        
        $healthEndpoint = "$healthUrl/actuator/health"
        Write-Info "Health endpoint: $healthEndpoint"
        
        $maxAttempts = 10
        $attempt = 0
        
        while ($attempt -lt $maxAttempts) {
            $attempt++
            try {
                $response = Invoke-RestMethod -Uri $healthEndpoint -Method Get -TimeoutSec 30
                if ($response.status -eq "UP") {
                    Write-Success "Health check passed: $($response.status)"
                    return
                } else {
                    Write-Info "Health check returned: $($response.status)... waiting 10 seconds"
                    Start-Sleep -Seconds 10
                }
            } catch {
                Write-Info "Health check failed (attempt $attempt/$maxAttempts)... waiting 10 seconds"
                Start-Sleep -Seconds 10
            }
        }
        
        throw "Health check failed after $maxAttempts attempts"
        
    } catch {
        Write-Error "Health check failed: $_"
        exit 1
    }
}

function Show-DeploymentInfo {
    Write-Step "Deployment Information"
    
    try {
        $status = railway status --json | ConvertFrom-Json
        $service = $status.services | Where-Object { $_.name -eq "chatwoot-backend" }
        
        if ($service) {
            Write-Info "Service URL: $($service.url)"
            Write-Info "Service Status: $($service.status)"
            Write-Info "Last Deployment: $($status.deployments[0].createdAt)"
            
            Write-ColorOutput "`n🚀 Chatwoot Backend deployed successfully!" $Green
            Write-ColorOutput "📊 Monitor your deployment at: https://railway.app/project/$RailwayProject" $Blue
            Write-ColorOutput "🔍 Health checks available at: $($service.url)/actuator/health" $Blue
            Write-ColorOutput "📈 Metrics available at: $($service.url)/actuator/metrics" $Blue
        } else {
            Write-Error "Could not retrieve service information"
        }
    } catch {
        Write-Error "Could not retrieve deployment information: $_"
    }
}

# Main execution
try {
    Write-Header "Chatwoot Railway Deployment"
    Write-Info "Environment: $Environment"
    Write-Info "Project: $RailwayProject"
    Write-Info "Log file: $LogFile"
    
    # Clear log file
    if (Test-Path $LogFile) {
        Remove-Item $LogFile -Force
    }
    
    Test-Prerequisites
    Build-Project
    Run-Tests
    Deploy-Railway
    Run-DatabaseMigrations
    Test-HealthChecks
    Show-DeploymentInfo
    
    Write-Header "Deployment Completed Successfully"
    
} catch {
    Write-Error "Deployment failed: $_"
    exit 1
}
