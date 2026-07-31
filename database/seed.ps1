#requires -Version 5.0

$ErrorActionPreference = 'Stop'

$ApiBaseUrl = $env:API_BASE_URL
if ([string]::IsNullOrWhiteSpace($ApiBaseUrl)) {
    $ApiBaseUrl = 'http://localhost:8080/api'
}

function Get-BackendErrorMessage {
    param(
        [Parameter(Mandatory = $true)]
        [System.Management.Automation.ErrorRecord]$ErrorRecord
    )

    $response = $ErrorRecord.Exception.Response
    if ($null -ne $response) {
        try {
            $reader = New-Object System.IO.StreamReader($response.GetResponseStream())
            $content = $reader.ReadToEnd()
            $reader.Dispose()

            if (-not [string]::IsNullOrWhiteSpace($content)) {
                $apiError = $content | ConvertFrom-Json
                if ($apiError.message) {
                    return $apiError.message
                }
            }
        }
        catch {
            # Si la respuesta no contiene JSON, se usa el mensaje original.
        }
    }

    return $ErrorRecord.Exception.Message
}

function Invoke-ApiRequest {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Resource,

        [Parameter(Mandatory = $true)]
        [ValidateSet('Get', 'Post', 'Patch')]
        [string]$Method,

        [Parameter(Mandatory = $true)]
        [string]$Path,

        [object]$Body
    )

    $uri = "$ApiBaseUrl$Path"

    try {
        $parameters = @{
            Uri         = $uri
            Method      = $Method
            ErrorAction = 'Stop'
        }

        if ($null -ne $Body) {
            $parameters.ContentType = 'application/json'
            $parameters.Body = $Body | ConvertTo-Json -Depth 5
        }

        return Invoke-RestMethod @parameters
    }
    catch {
        $message = Get-BackendErrorMessage -ErrorRecord $_
        Write-Host "Error al procesar ${Resource}: $message" -ForegroundColor Red
        exit 1
    }
}

function Assert-LoanState {
    param(
        [Parameter(Mandatory = $true)]
        [object]$Loan,

        [Parameter(Mandatory = $true)]
        [string]$ExpectedState,

        [Parameter(Mandatory = $true)]
        [string]$Resource
    )

    if ($null -eq $Loan -or $Loan.estadoPrestamo -ne $ExpectedState) {
        $receivedState = if ($null -eq $Loan) { '<sin respuesta>' } else { $Loan.estadoPrestamo }
        Write-Host "[ERROR] Estado invalido para ${Resource}. Se esperaba $ExpectedState y se recibio $receivedState." -ForegroundColor Red
        exit 1
    }
}

Write-Host 'Verificando disponibilidad del backend...'
try {
    Invoke-RestMethod -Uri "$ApiBaseUrl/usuarios" -Method Get -ErrorAction Stop | Out-Null
}
catch {
    $message = Get-BackendErrorMessage -ErrorRecord $_
    Write-Host "No se pudo conectar con el backend en $ApiBaseUrl. $message" -ForegroundColor Red
    exit 1
}

Write-Host 'Creando usuarios...'
$ana = Invoke-ApiRequest -Resource 'usuario Ana Gomez' -Method Post -Path '/usuarios' -Body @{
    nombre = 'Ana'; apellido = 'Gómez'; email = 'ana.gomez@example.com'; fechaNacimiento = '1990-05-12'
}
$carlos = Invoke-ApiRequest -Resource 'usuario Carlos Perez' -Method Post -Path '/usuarios' -Body @{
    nombre = 'Carlos'; apellido = 'Pérez'; email = 'carlos.perez@example.com'; fechaNacimiento = '1985-09-23'
}
$laura = Invoke-ApiRequest -Resource 'usuario Laura Torres' -Method Post -Path '/usuarios' -Body @{
    nombre = 'Laura'; apellido = 'Torres'; email = 'laura.torres@example.com'; fechaNacimiento = '1992-03-08'
}
$miguel = Invoke-ApiRequest -Resource 'usuario Miguel Ruiz' -Method Post -Path '/usuarios' -Body @{
    nombre = 'Miguel'; apellido = 'Ruiz'; email = 'miguel.ruiz@example.com'; fechaNacimiento = '1988-11-17'
}
Write-Host '[OK] Usuarios creados' -ForegroundColor Green

Write-Host 'Creando libros...'
$cleanCode = Invoke-ApiRequest -Resource 'libro Clean Code' -Method Post -Path '/libros' -Body @{
    titulo = 'Clean Code'; isbn = '9780132350884'; edicion = '1st Edition'; fechaPublicacion = '2008-08-01'; autor = 'Robert C. Martin'
}
$cienAnios = Invoke-ApiRequest -Resource 'libro Cien anos de soledad' -Method Post -Path '/libros' -Body @{
    titulo = 'Cien años de soledad'; isbn = '9780307474728'; edicion = 'Edición conmemorativa'; fechaPublicacion = '1967-05-30'; autor = 'Gabriel García Márquez'
}
$principito = Invoke-ApiRequest -Resource 'libro El Principito' -Method Post -Path '/libros' -Body @{
    titulo = 'El Principito'; isbn = '9780156012195'; edicion = 'Edición estándar'; fechaPublicacion = '1943-04-06'; autor = 'Antoine de Saint-Exupéry'
}
$donQuijote = Invoke-ApiRequest -Resource 'libro Don Quijote' -Method Post -Path '/libros' -Body @{
    titulo = 'Don Quijote'; isbn = '9788420412146'; edicion = 'Edición conmemorativa'; fechaPublicacion = '1605-01-16'; autor = 'Miguel de Cervantes'
}
Write-Host '[OK] Libros creados' -ForegroundColor Green

Write-Host 'Creando ejemplares...'
$ejemplares = @()
foreach ($codigo in @('LIB-000001', 'LIB-000002', 'LIB-000003')) {
    $ejemplares += Invoke-ApiRequest -Resource "ejemplar $codigo" -Method Post -Path "/libros/$($cleanCode.id)/ejemplares" -Body @{ codigoInventario = $codigo }
}
foreach ($codigo in @('LIB-000004', 'LIB-000005', 'LIB-000006')) {
    $ejemplares += Invoke-ApiRequest -Resource "ejemplar $codigo" -Method Post -Path "/libros/$($cienAnios.id)/ejemplares" -Body @{ codigoInventario = $codigo }
}
foreach ($codigo in @('LIB-000007', 'LIB-000008')) {
    $ejemplares += Invoke-ApiRequest -Resource "ejemplar $codigo" -Method Post -Path "/libros/$($principito.id)/ejemplares" -Body @{ codigoInventario = $codigo }
}
Write-Host '[OK] Ejemplares creados' -ForegroundColor Green

$today = (Get-Date).Date
$fechaActivaPrestamo = $today.AddDays(-2).ToString('yyyy-MM-dd')
$fechaActivaDevolucion = $today.AddDays(10).ToString('yyyy-MM-dd')
$fechaVencidaPrestamo = $today.AddDays(-20).ToString('yyyy-MM-dd')
$fechaVencidaDevolucion = $today.AddDays(-10).ToString('yyyy-MM-dd')
$fechaDevueltaPrestamo = $today.AddDays(-30).ToString('yyyy-MM-dd')
$fechaDevueltaDevolucion = $today.AddDays(-20).ToString('yyyy-MM-dd')

Write-Host 'Creando prestamos...'
$prestamoActivo = Invoke-ApiRequest -Resource 'prestamo ACTIVO' -Method Post -Path '/prestamos' -Body @{
    usuarioId = $ana.id; ejemplarId = $ejemplares[0].id; fechaPrestamo = $fechaActivaPrestamo; fechaDevolucion = $fechaActivaDevolucion
}
$prestamoVencido = Invoke-ApiRequest -Resource 'prestamo VENCIDO' -Method Post -Path '/prestamos' -Body @{
    usuarioId = $carlos.id; ejemplarId = $ejemplares[3].id; fechaPrestamo = $fechaVencidaPrestamo; fechaDevolucion = $fechaVencidaDevolucion
}
$prestamoDevueltoUno = Invoke-ApiRequest -Resource 'primer prestamo DEVUELTO' -Method Post -Path '/prestamos' -Body @{
    usuarioId = $laura.id; ejemplarId = $ejemplares[1].id; fechaPrestamo = $fechaDevueltaPrestamo; fechaDevolucion = $fechaDevueltaDevolucion
}
$respuestaDevolucionUno = Invoke-ApiRequest -Resource 'devolucion del primer prestamo' -Method Patch -Path "/prestamos/$($prestamoDevueltoUno.id)/devolver"
Assert-LoanState -Loan $respuestaDevolucionUno -ExpectedState 'DEVUELTO' -Resource 'primer prestamo devuelto'

$prestamoDevueltoDos = Invoke-ApiRequest -Resource 'segundo prestamo DEVUELTO' -Method Post -Path '/prestamos' -Body @{
    usuarioId = $laura.id; ejemplarId = $ejemplares[4].id; fechaPrestamo = $fechaDevueltaPrestamo; fechaDevolucion = $fechaDevueltaDevolucion
}
$respuestaDevolucionDos = Invoke-ApiRequest -Resource 'devolucion del segundo prestamo' -Method Patch -Path "/prestamos/$($prestamoDevueltoDos.id)/devolver"
Assert-LoanState -Loan $respuestaDevolucionDos -ExpectedState 'DEVUELTO' -Resource 'segundo prestamo devuelto'

$loanStates = @(
    $prestamoActivo.estadoPrestamo,
    $prestamoVencido.estadoPrestamo,
    $respuestaDevolucionUno.estadoPrestamo,
    $respuestaDevolucionDos.estadoPrestamo
)
$activeCount = @($loanStates | Where-Object { $_ -eq 'ACTIVO' }).Count
$overdueCount = @($loanStates | Where-Object { $_ -eq 'VENCIDO' }).Count
$returnedCount = @($loanStates | Where-Object { $_ -eq 'DEVUELTO' }).Count

if ($activeCount -ne 1 -or $overdueCount -ne 1 -or $returnedCount -ne 2) {
    Write-Host "[ERROR] Estados de prestamos invalidos. ACTIVO=$activeCount, VENCIDO=$overdueCount, DEVUELTO=$returnedCount." -ForegroundColor Red
    exit 1
}

Write-Host '[OK] Prestamos creados y devoluciones registradas' -ForegroundColor Green

Write-Host ''
Write-Host '[OK] Seed completado correctamente.' -ForegroundColor Green
Write-Host 'Resumen:'
Write-Host '  Usuarios: 4 (Miguel Ruiz no tiene prestamos)'
Write-Host '  Libros: 4 (Don Quijote no tiene ejemplares)'
Write-Host '  Ejemplares: 8'
Write-Host '  Prestamos: 1 ACTIVO, 1 VENCIDO, 2 DEVUELTOS'
Write-Host ''
Write-Host 'Ahora puedes generar database/library_test.dump con:'
Write-Host 'docker compose exec -T mysql sh -c ''exec mysqldump -u root -p"$MYSQL_ROOT_PASSWORD" --databases "$MYSQL_DATABASE" --routines --triggers --single-transaction --set-gtid-purged=OFF'' > database/library_test.dump'
