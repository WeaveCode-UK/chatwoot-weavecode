# Chatwoot Railway Environment Setup Script
# Sets up environment variables and project configuration for Railway deployment

param(
    [Parameter(Mandatory=$false)]
    [string]$Environment = "production",
    
    [Parameter(Mandatory=$false)]
    [string]$RailwayProject = "chatwoot-weavecode",
    
    [Parameter(Mandatory=$false)]
    [switch]$Interactive,
    
    [Parameter(Mandatory=$false)]
    [switch]$FromEnvFile
)

# Configuration
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$ProjectRoot = Split-Path -Parent $ScriptDir
$EnvFile = Join-Path $ProjectRoot ".env"
$LogFile = Join-Path $ProjectRoot "setup.log"

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

function Test-RailwayCLI {
    Write-Step "Testing Railway CLI"
    
    try {
        $railwayVersion = railway --version 2>$null
        if ($railwayVersion) {
            Write-Success "Railway CLI found: $railwayVersion"
            return $true
        } else {
            throw "Railway CLI not found"
        }
    } catch {
        Write-Error "Railway CLI not installed"
        Write-Info "Please install Railway CLI first:"
        Write-Info "npm install -g @railway/cli"
        return $false
    }
}

function Test-RailwayAuth {
    Write-Step "Testing Railway Authentication"
    
    try {
        $loginStatus = railway whoami 2>$null
        
        if (-not $loginStatus -or $loginStatus -like "*not logged in*") {
            Write-Error "Not logged into Railway"
            Write-Info "Please run 'railway login' first"
            return $false
        }
        
        Write-Success "Authenticated as: $loginStatus"
        return $true
    } catch {
        Write-Error "Authentication check failed: $_"
        return $false
    }
}

function Get-EnvironmentVariables {
    Write-Step "Setting up Environment Variables"
    
    $envVars = @{}
    
    if ($FromEnvFile -and (Test-Path $EnvFile)) {
        Write-Info "Loading environment variables from .env file..."
        Get-Content $EnvFile | ForEach-Object {
            if ($_ -match '^([^#][^=]+)=(.*)$') {
                $key = $matches[1].Trim()
                $value = $matches[2].Trim()
                $envVars[$key] = $value
            }
        }
        Write-Success "Loaded $(($envVars.Keys | Measure-Object).Count) variables from .env file"
    } else {
        Write-Info "Setting up environment variables interactively..."
        
        # Database Configuration
        $envVars["DATABASE_URL"] = Read-Host "Enter DATABASE_URL (e.g., jdbc:postgresql://host:port/db)"
        $envVars["DATABASE_USERNAME"] = Read-Host "Enter DATABASE_USERNAME"
        $envVars["DATABASE_PASSWORD"] = Read-Host "Enter DATABASE_PASSWORD" -AsSecureString | ConvertFrom-SecureString
        
        # Redis Configuration
        $envVars["REDIS_HOST"] = Read-Host "Enter REDIS_HOST (default: localhost)"
        if (-not $envVars["REDIS_HOST"]) { $envVars["REDIS_HOST"] = "localhost" }
        $envVars["REDIS_PORT"] = Read-Host "Enter REDIS_PORT (default: 6379)"
        if (-not $envVars["REDIS_PORT"]) { $envVars["REDIS_PORT"] = "6379" }
        $envVars["REDIS_PASSWORD"] = Read-Host "Enter REDIS_PASSWORD (optional)" -AsSecureString | ConvertFrom-SecureString
        
        # JWT Configuration
        $envVars["JWT_SECRET"] = Read-Host "Enter JWT_SECRET (256-bit secret key)"
        $envVars["JWT_EXPIRATION"] = Read-Host "Enter JWT_EXPIRATION in milliseconds (default: 3600000)"
        if (-not $envVars["JWT_EXPIRATION"]) { $envVars["JWT_EXPIRATION"] = "3600000" }
        
        # API Keys
        $envVars["OPENAI_API_KEY"] = Read-Host "Enter OPENAI_API_KEY (optional)"
        $envVars["STRIPE_API_KEY"] = Read-Host "Enter STRIPE_API_KEY (optional)"
        $envVars["PAYPAL_CLIENT_ID"] = Read-Host "Enter PAYPAL_CLIENT_ID (optional)"
        $envVars["PAYPAL_CLIENT_SECRET"] = Read-Host "Enter PAYPAL_CLIENT_SECRET (optional)" -AsSecureString | ConvertFrom-SecureString
        $envVars["SENDGRID_API_KEY"] = Read-Host "Enter SENDGRID_API_KEY (optional)"
        
        # Environment
        $envVars["SPRING_PROFILES_ACTIVE"] = $Environment
        $envVars["PORT"] = "8080"
        
        # Save to .env file
        Write-Info "Saving environment variables to .env file..."
        $envContent = @()
        foreach ($key in $envVars.Keys) {
            $value = $envVars[$key]
            if ($value) {
                $envContent += "$key=$value"
            }
        }
        $envContent | Out-File -FilePath $EnvFile -Encoding UTF8
        Write-Success "Environment variables saved to .env file"
    }
    
    return $envVars
}

function Set-RailwayEnvironment {
    param([hashtable]$EnvVars)
    
    Write-Step "Setting Railway Environment Variables"
    
    try {
        # Link project if not already linked
        Write-Info "Linking Railway project: $RailwayProject"
        railway link --project $RailwayProject
        
        # Set environment variables
        Write-Info "Setting environment variables in Railway..."
        
        foreach ($key in $EnvVars.Keys) {
            $value = $EnvVars[$key]
            if ($value) {
                Write-Info "Setting $key..."
                railway variables set "$key=$value" --service chatwoot-backend
                
                if ($LASTEXITCODE -eq 0) {
                    Write-Success "Set $key"
                } else {
                    Write-Error "Failed to set $key"
                }
            }
        }
        
        Write-Success "Environment variables configured in Railway"
        
    } catch {
        Write-Error "Failed to set Railway environment: $_"
        throw
    }
}

function Test-RailwayConnection {
    Write-Step "Testing Railway Connection"
    
    try {
        Write-Info "Testing Railway project connection..."
        $status = railway status --json | ConvertFrom-Json
        
        if ($status) {
            Write-Success "Successfully connected to Railway project"
            Write-Info "Project: $($status.name)"
            Write-Info "Services: $(($status.services | Measure-Object).Count)"
            return $true
        } else {
            throw "Could not retrieve project status"
        }
        
    } catch {
        Write-Error "Railway connection test failed: $_"
        return $false
    }
}

function Show-EnvironmentSummary {
    param([hashtable]$EnvVars)
    
    Write-Step "Environment Configuration Summary"
    
    Write-Info "Environment: $Environment"
    Write-Info "Railway Project: $RailwayProject"
    Write-Info "Total Variables: $(($envVars.Keys | Measure-Object).Count)"
    
    Write-ColorOutput "`n📋 Configured Environment Variables:" $Blue
    
    $categories = @{
        "Database" = @("DATABASE_URL", "DATABASE_USERNAME", "DATABASE_PASSWORD")
        "Redis" = @("REDIS_HOST", "REDIS_PORT", "REDIS_PASSWORD")
        "JWT" = @("JWT_SECRET", "JWT_EXPIRATION")
        "API Keys" = @("OPENAI_API_KEY", "STRIPE_API_KEY", "PAYPAL_CLIENT_ID", "PAYPAL_CLIENT_SECRET", "SENDGRID_API_KEY")
        "System" = @("SPRING_PROFILES_ACTIVE", "PORT")
    }
    
    foreach ($category in $categories.Keys) {
        Write-ColorOutput "`n🔹 $category:" $Yellow
        foreach ($var in $categories[$category]) {
            if ($envVars.ContainsKey($var)) {
                $value = $envVars[$var]
                if ($var -like "*PASSWORD*" -or $var -like "*SECRET*" -or $var -like "*KEY*") {
                    $displayValue = "***HIDDEN***"
                } else {
                    $displayValue = $value
                }
                Write-ColorOutput "  $var = $displayValue" $Green
            }
        }
    }
    
    Write-ColorOutput "`n🚀 Next Steps:" $Blue
    Write-ColorOutput "1. Run the deployment script: .\scripts\deploy-railway.ps1" $Green
    Write-ColorOutput "2. Monitor deployment at: https://railway.app/project/$RailwayProject" $Green
    Write-ColorOutput "3. Check health endpoint after deployment" $Green
}

function Create-RailwayProject {
    Write-Step "Creating Railway Project"
    
    try {
        Write-Info "Creating new Railway project: $RailwayProject"
        
        $response = Read-Host "Do you want to create a new Railway project? (y/N)"
        if ($response -eq "y" -or $response -eq "Y") {
            railway init --name $RailwayProject
            
            if ($LASTEXITCODE -eq 0) {
                Write-Success "Railway project created: $RailwayProject"
            } else {
                throw "Failed to create Railway project"
            }
        } else {
            Write-Info "Skipping project creation"
        }
        
    } catch {
        Write-Error "Project creation failed: $_"
        throw
    }
}

# Main execution
try {
    Write-Header "Chatwoot Railway Environment Setup"
    Write-Info "Environment: $Environment"
    Write-Info "Project: $RailwayProject"
    Write-Info "Log file: $LogFile"
    
    # Clear log file
    if (Test-Path $LogFile) {
        Remove-Item $LogFile -Force
    }
    
    # Test prerequisites
    if (-not (Test-RailwayCLI)) {
        exit 1
    }
    
    if (-not (Test-RailwayAuth)) {
        exit 1
    }
    
    # Create project if needed
    Create-RailwayProject
    
    # Test connection
    if (-not (Test-RailwayConnection)) {
        exit 1
    }
    
    # Get environment variables
    $envVars = Get-EnvironmentVariables
    
    # Set Railway environment
    Set-RailwayEnvironment -EnvVars $envVars
    
    # Show summary
    Show-EnvironmentSummary -EnvVars $envVars
    
    Write-Header "Environment Setup Completed Successfully"
    
} catch {
    Write-Error "Environment setup failed: $_"
    exit 1
}
