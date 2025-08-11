#!/usr/bin/env pwsh

<#
.SYNOPSIS
    Script para executar todos os tipos de testes do projeto Chatwoot

.DESCRIPTION
    Este script executa testes unitários, de integração e de performance
    para o backend multi-tenant do Chatwoot.

.PARAMETER TestType
    Tipo de teste a executar: unit, integration, performance, all

.PARAMETER Clean
    Limpar cache e recompilar antes dos testes

.PARAMETER Report
    Gerar relatório detalhado dos testes

.EXAMPLE
    .\run-tests.ps1 -TestType all
    .\run-tests.ps1 -TestType unit -Clean
    .\run-tests.ps1 -TestType performance -Report
#>

param(
    [Parameter(Mandatory = $false)]
    [ValidateSet("unit", "integration", "performance", "all")]
    [string]$TestType = "all",
    
    [Parameter(Mandatory = $false)]
    [switch]$Clean,
    
    [Parameter(Mandatory = $false)]
    [switch]$Report
)

# Configurações
$ProjectRoot = Split-Path -Parent $PSScriptRoot
$TestResultsDir = Join-Path $ProjectRoot "test-results"
$ReportsDir = Join-Path $ProjectRoot "reports"

# Cores para output
$Colors = @{
    Success = "Green"
    Error = "Red"
    Warning = "Yellow"
    Info = "Cyan"
    Header = "Magenta"
}

function Write-Header {
    param([string]$Message)
    Write-Host "`n" -NoNewline
    Write-Host "=" * 80 -ForegroundColor $Colors.Header
    Write-Host " $Message" -ForegroundColor $Colors.Header
    Write-Host "=" * 80 -ForegroundColor $Colors.Header
    Write-Host ""
}

function Write-Success {
    param([string]$Message)
    Write-Host "✅ $Message" -ForegroundColor $Colors.Success
}

function Write-Error {
    param([string]$Message)
    Write-Host "❌ $Message" -ForegroundColor $Colors.Error
}

function Write-Warning {
    param([string]$Message)
    Write-Host "⚠️  $Message" -ForegroundColor $Colors.Warning
}

function Write-Info {
    param([string]$Message)
    Write-Host "ℹ️  $Message" -ForegroundColor $Colors.Info
}

function Test-Prerequisites {
    Write-Header "Verificando Pré-requisitos"
    
    # Verificar Java
    try {
        $javaVersion = java -version 2>&1 | Select-String "version"
        if ($javaVersion) {
            Write-Success "Java encontrado: $javaVersion"
        } else {
            Write-Error "Java não encontrado"
            return $false
        }
    } catch {
        Write-Error "Java não encontrado"
        return $false
    }
    
    # Verificar Maven
    try {
        $mvnVersion = mvn -version 2>&1 | Select-String "Apache Maven"
        if ($mvnVersion) {
            Write-Success "Maven encontrado: $mvnVersion"
        } else {
            Write-Warning "Maven não encontrado - alguns testes podem falhar"
        }
    } catch {
        Write-Warning "Maven não encontrado - alguns testes podem falhar"
    }
    
    # Verificar Docker
    try {
        $dockerVersion = docker --version 2>&1
        if ($dockerVersion) {
            Write-Success "Docker encontrado: $dockerVersion"
        } else {
            Write-Warning "Docker não encontrado - testes de integração podem falhar"
        }
    } catch {
        Write-Warning "Docker não encontrado - testes de integração podem falhar"
    }
    
    return $true
}

function Clean-Project {
    Write-Header "Limpando Projeto"
    
    try {
        # Limpar diretórios de build
        $buildDirs = @("target", "bin", "out")
        foreach ($dir in $buildDirs) {
            $buildPath = Join-Path $ProjectRoot $dir
            if (Test-Path $buildPath) {
                Remove-Item -Path $buildPath -Recurse -Force
                Write-Success "Diretório $dir removido"
            }
        }
        
        # Limpar cache Maven
        if (Get-Command mvn -ErrorAction SilentlyContinue) {
            mvn clean
            Write-Success "Cache Maven limpo"
        }
        
        Write-Success "Projeto limpo com sucesso"
    } catch {
        Write-Error "Erro ao limpar projeto: $_"
    }
}

function Run-UnitTests {
    Write-Header "Executando Testes Unitários"
    
    try {
        if (Get-Command mvn -ErrorAction SilentlyContinue) {
            $testResults = mvn test -Dtest="*Test" -DfailIfNoTests=false 2>&1
            $testResults | ForEach-Object { Write-Host $_ }
            
            if ($LASTEXITCODE -eq 0) {
                Write-Success "Testes unitários executados com sucesso"
                return $true
            } else {
                Write-Error "Testes unitários falharam"
                return $false
            }
        } else {
            Write-Warning "Maven não encontrado - pulando testes unitários"
            return $false
        }
    } catch {
        Write-Error "Erro ao executar testes unitários: $_"
        return $false
    }
}

function Run-IntegrationTests {
    Write-Header "Executando Testes de Integração"
    
    try {
        if (Get-Command mvn -ErrorAction SilentlyContinue) {
            $testResults = mvn test -Dtest="*IntegrationTest" -DfailIfNoTests=false 2>&1
            $testResults | ForEach-Object { Write-Host $_ }
            
            if ($LASTEXITCODE -eq 0) {
                Write-Success "Testes de integração executados com sucesso"
                return $true
            } else {
                Write-Error "Testes de integração falharam"
                return $false
            }
        } else {
            Write-Warning "Maven não encontrado - pulando testes de integração"
            return $false
        }
    } catch {
        Write-Error "Erro ao executar testes de integração: $_"
        return $false
    }
}

function Run-PerformanceTests {
    Write-Header "Executando Testes de Performance"
    
    try {
        # Verificar se o Gatling está disponível
        $gatlingPath = Join-Path $ProjectRoot "target\gatling"
        if (Test-Path $gatlingPath) {
            Write-Info "Executando testes de performance com Gatling..."
            
            # Executar testes de performance
            $testResults = mvn gatling:test -Dgatling.simulationClass="com.weavecode.chatwoot.performance.TenantPerformanceTest" 2>&1
            $testResults | ForEach-Object { Write-Host $_ }
            
            if ($LASTEXITCODE -eq 0) {
                Write-Success "Testes de performance executados com sucesso"
                return $true
            } else {
                Write-Error "Testes de performance falharam"
                return $false
            }
        } else {
            Write-Warning "Gatling não encontrado - pulando testes de performance"
            return $false
        }
    } catch {
        Write-Error "Erro ao executar testes de performance: $_"
        return $false
    }
}

function Generate-TestReport {
    Write-Header "Gerando Relatório de Testes"
    
    try {
        # Criar diretórios se não existirem
        if (!(Test-Path $TestResultsDir)) {
            New-Item -ItemType Directory -Path $TestResultsDir -Force | Out-Null
        }
        if (!(Test-Path $ReportsDir)) {
            New-Item -ItemType Directory -Path $ReportsDir -Force | Out-Null
        }
        
        # Gerar relatório Maven
        if (Get-Command mvn -ErrorAction SilentlyContinue) {
            mvn surefire-report:report 2>&1 | Out-Null
            
            # Copiar relatórios para diretório de reports
            $surefireReports = Join-Path $ProjectRoot "target\site"
            if (Test-Path $surefireReports) {
                Copy-Item -Path "$surefireReports\*" -Destination $ReportsDir -Recurse -Force
                Write-Success "Relatórios copiados para $ReportsDir"
            }
        }
        
        # Gerar relatório de cobertura se disponível
        if (Get-Command mvn -ErrorAction SilentlyContinue) {
            mvn jacoco:report 2>&1 | Out-Null
            
            $jacocoReports = Join-Path $ProjectRoot "target\site\jacoco"
            if (Test-Path $jacocoReports) {
                Copy-Item -Path "$jacocoReports\*" -Destination $ReportsDir -Recurse -Force
                Write-Success "Relatório de cobertura copiado para $ReportsDir"
            }
        }
        
        Write-Success "Relatório de testes gerado com sucesso"
        Write-Info "Relatórios disponíveis em: $ReportsDir"
        
    } catch {
        Write-Error "Erro ao gerar relatório: $_"
    }
}

function Show-TestSummary {
    Write-Header "Resumo dos Testes"
    
    $summary = @{
        Unit = $script:UnitTestsPassed
        Integration = $script:IntegrationTestsPassed
        Performance = $script:PerformanceTestsPassed
    }
    
    foreach ($testType in $summary.Keys) {
        $status = if ($summary[$testType]) { "✅ PASSOU" } else { "❌ FALHOU" }
        Write-Host "$testType Tests: $status" -ForegroundColor $(if ($summary[$testType]) { $Colors.Success } else { $Colors.Error })
    }
    
    $allPassed = $summary.Values -notcontains $false
    if ($allPassed) {
        Write-Success "Todos os testes passaram com sucesso!"
    } else {
        Write-Error "Alguns testes falharam. Verifique os logs acima."
    }
}

# Script principal
Write-Header "Chatwoot - Executor de Testes"
Write-Info "Tipo de teste: $TestType"
Write-Info "Limpar projeto: $Clean"
Write-Info "Gerar relatório: $Report"

# Verificar pré-requisitos
if (!(Test-Prerequisites)) {
    Write-Error "Pré-requisitos não atendidos. Abortando execução."
    exit 1
}

# Limpar projeto se solicitado
if ($Clean) {
    Clean-Project
}

# Executar testes baseado no tipo
$script:UnitTestsPassed = $false
$script:IntegrationTestsPassed = $false
$script:PerformanceTestsPassed = $false

switch ($TestType) {
    "unit" {
        $script:UnitTestsPassed = Run-UnitTests
    }
    "integration" {
        $script:IntegrationTestsPassed = Run-IntegrationTests
    }
    "performance" {
        $script:PerformanceTestsPassed = Run-PerformanceTests
    }
    "all" {
        $script:UnitTestsPassed = Run-UnitTests
        $script:IntegrationTestsPassed = Run-IntegrationTests
        $script:PerformanceTestsPassed = Run-PerformanceTests
    }
}

# Gerar relatório se solicitado
if ($Report) {
    Generate-TestReport
}

# Mostrar resumo
Show-TestSummary

# Retornar código de saída apropriado
$allPassed = $script:UnitTestsPassed -and $script:IntegrationTestsPassed -and $script:PerformanceTestsPassed
exit $(if ($allPassed) { 0 } else { 1 })
