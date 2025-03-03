const express = require('express');
const bodyParser = require('body-parser');
const chalk = require('chalk');
const readlineSync = require('readline-sync');
const fs = require('fs');
const path = require('path');

// Inicializar Express
const app = express();
const PORT = 8082;

// Configuración de middleware
app.use(bodyParser.json());

// Almacenamiento de transacciones (en memoria)
let transacciones = [];
const MAX_INTENTOS = 3;
const ARCHIVO_TRANSACCIONES = path.join(__dirname, 'transacciones.json');

// Control de modo de prueba automático
let modoAutomaticoActivo = false;
let intervaloPruebas = null;

// Cargar transacciones almacenadas previamente si existen
function cargarTransacciones() {
    try {
        if (fs.existsSync(ARCHIVO_TRANSACCIONES)) {
            const data = fs.readFileSync(ARCHIVO_TRANSACCIONES, 'utf8');
            transacciones = JSON.parse(data);
            console.log(chalk.blue(`Se cargaron ${transacciones.length} transacciones previas`));
        }
    } catch (error) {
        console.error(chalk.red('Error al cargar transacciones:'), error);
    }
}

// Guardar transacciones en archivo
function guardarTransacciones() {
    try {
        fs.writeFileSync(ARCHIVO_TRANSACCIONES, JSON.stringify(transacciones, null, 2), 'utf8');
    } catch (error) {
        console.error(chalk.red('Error al guardar transacciones:'), error);
    }
}

// Generar una transacción de prueba
function generarTransaccionPrueba() {
    return {
        codTransaccion: 'TEST' + Math.floor(Math.random() * 10000),
        tipo: 'REC',
        marca: 'VISA',
        monto: (Math.random() * 500 + 50).toFixed(2),
        codigoUnicoTransaccion: 'UNIQ' + Date.now(),
        fecha: new Date().toISOString(),
        estado: 'PEN',
        moneda: 'USD',
        pais: 'EC',
        tarjeta: 4532123456789012,
        fechaCaducidad: '2025-12-31',
        swiftBanco: 'PICHECEQ',
        cuentaIban: 'EC012345678901234567890',
        diferido: false,
        fechaRecepcion: new Date().toISOString(),
        fechaUltimoIntento: new Date().toISOString(),
        intentos: 1,
        procesada: false
    };
}

// Función para agregar transacción de prueba automáticamente
function agregarTransaccionPrueba() {
    const transaccion = generarTransaccionPrueba();
    transacciones.push(transaccion);
    guardarTransacciones();
    console.log(chalk.green(`\n✓ Nueva transacción de prueba generada automáticamente: ${transaccion.codTransaccion}`));
    return transaccion;
}

// Iniciar el modo automático de pruebas
function iniciarModoAutomatico() {
    if (!modoAutomaticoActivo) {
        modoAutomaticoActivo = true;
        console.log(chalk.green('Modo automático de pruebas iniciado. Se generará una nueva transacción cada 2 minutos.'));
        
        // Generar la primera transacción inmediatamente
        agregarTransaccionPrueba();
        
        // Configurar el intervalo para generar una transacción cada 2 minutos
        intervaloPruebas = setInterval(() => {
            agregarTransaccionPrueba();
        }, 120000); // 2 minutos = 120000 ms
    } else {
        console.log(chalk.yellow('El modo automático de pruebas ya está activo.'));
    }
}

// Detener el modo automático de pruebas
function detenerModoAutomatico() {
    if (modoAutomaticoActivo) {
        modoAutomaticoActivo = false;
        if (intervaloPruebas) {
            clearInterval(intervaloPruebas);
            intervaloPruebas = null;
        }
        console.log(chalk.green('Modo automático de pruebas detenido.'));
    } else {
        console.log(chalk.yellow('El modo automático de pruebas no está activo.'));
    }
}

// Endpoint para recibir transacciones
app.post('/api/v1/transacciones', (req, res) => {
    const transaccion = req.body;
    
    // Validar la transacción
    if (!transaccion || !transaccion.codTransaccion) {
        return res.status(400).json({ 
            error: 'Transacción inválida', 
            mensaje: 'La transacción debe incluir un código de transacción'
        });
    }
    
    // Verificar si ya existe para manejar reintentos
    const existeIndex = transacciones.findIndex(t => t.codTransaccion === transaccion.codTransaccion);
    
    if (existeIndex >= 0) {
        // Es un reintento, actualizar fecha e incrementar contador
        transacciones[existeIndex].fechaUltimoIntento = new Date().toISOString();
        transacciones[existeIndex].intentos += 1;
        transacciones[existeIndex].estado = 'PEN'; // Volver a pendiente
    } else {
        // Nueva transacción
        transaccion.fechaRecepcion = new Date().toISOString();
        transaccion.fechaUltimoIntento = new Date().toISOString();
        transaccion.estado = 'PEN'; // Pendiente
        transaccion.intentos = 1;
        transaccion.procesada = false;
        
        // Añadir a la lista
        transacciones.push(transaccion);
    }
    
    // Guardar transacciones
    guardarTransacciones();
    
    console.log(chalk.green('✓ Transacción recibida:'), chalk.yellow(transaccion.codTransaccion));
    
    return res.status(200).json({
        mensaje: 'Transacción recibida correctamente',
        codigoTransaccion: transaccion.codTransaccion
    });
});

// Endpoint para generar una nueva transacción de prueba
app.post('/api/v1/transacciones/prueba', (req, res) => {
    console.log(chalk.blue('Solicitud recibida para generar una transacción de prueba'));
    
    const transaccion = agregarTransaccionPrueba();
    
    return res.status(200).json({
        mensaje: 'Transacción de prueba generada correctamente',
        transaccion: transaccion
    });
});

// Endpoint para activar/desactivar el modo automático de pruebas
app.post('/api/v1/transacciones/modoautomatico', (req, res) => {
    const activar = req.body && req.body.activar;
    
    if (activar) {
        iniciarModoAutomatico();
        return res.status(200).json({
            mensaje: 'Modo automático de pruebas activado correctamente'
        });
    } else {
        detenerModoAutomatico();
        return res.status(200).json({
            mensaje: 'Modo automático de pruebas desactivado correctamente'
        });
    }
});

// Iniciar el servidor
const server = app.listen(PORT, () => {
    console.log(chalk.green(`Servidor de simulación iniciado en http://localhost:${PORT}`));
    console.log(chalk.yellow('Presiona Ctrl+C para salir'));
    
    // Cargar transacciones existentes
    cargarTransacciones();
    
    // Iniciar el menú de procesamiento
    setTimeout(() => {
        mostrarMenu();
    }, 1000);
});

// Función principal del menú
function mostrarMenu() {
    console.clear();
    console.log(chalk.blue.bold('=== SIMULADOR DE MICROSERVICIO DE TRANSACCIÓN SIMPLE ==='));
    console.log(chalk.blue(`Total de transacciones: ${transacciones.length}`));
    
    const pendientes = transacciones.filter(t => t.estado === 'PEN' && !t.procesada);
    console.log(chalk.yellow(`Transacciones pendientes: ${pendientes.length}`));
    
    // Mostrar el estado del modo automático
    if (modoAutomaticoActivo) {
        console.log(chalk.green('Modo automático de pruebas: ACTIVO'));
        console.log(chalk.green('Se generará una nueva transacción automáticamente cada 2 minutos'));
    } else {
        console.log(chalk.blue('Modo automático de pruebas: INACTIVO'));
    }
    
    const opciones = [
        'Ver transacciones pendientes',
        'Procesar transacciones pendientes',
        'Ver historial de transacciones',
        'Limpiar todas las transacciones',
        'Activar modo automático de pruebas',
        'Desactivar modo automático de pruebas',
        'Salir'
    ];
    
    const opcionSeleccionada = readlineSync.keyInSelect(opciones, 'Selecciona una opción:');
    
    switch (opcionSeleccionada) {
        case 0:
            verTransaccionesPendientes();
            break;
        case 1:
            procesarTransacciones();
            break;
        case 2:
            verHistorialTransacciones();
            break;
        case 3:
            limpiarTransacciones();
            break;
        case 4:
            iniciarModoAutomatico();
            setTimeout(() => {
                mostrarMenu();
            }, 500);
            break;
        case 5:
            detenerModoAutomatico();
            setTimeout(() => {
                mostrarMenu();
            }, 500);
            break;
        case 6:
            // Detener el modo automático si está activo
            if (modoAutomaticoActivo && intervaloPruebas) {
                console.log(chalk.yellow('\nDeteniendo modo automático...'));
                clearInterval(intervaloPruebas);
                modoAutomaticoActivo = false;
            }
            console.log(chalk.red('Cerrando el simulador...'));
            process.exit(0);
            break;
        default:
            mostrarMenu();
    }
}

// Ver transacciones pendientes
function verTransaccionesPendientes() {
    console.clear();
    console.log(chalk.blue.bold('=== TRANSACCIONES PENDIENTES ==='));
    
    const pendientes = transacciones.filter(t => t.estado === 'PEN' && !t.procesada);
    
    if (pendientes.length === 0) {
        console.log(chalk.yellow('No hay transacciones pendientes'));
    } else {
        pendientes.forEach((t, index) => {
            console.log(chalk.green(`\n[${index + 1}] Transacción: ${t.codTransaccion}`));
            console.log(chalk.white(`    Monto: ${t.monto} ${t.moneda}`));
            console.log(chalk.white(`    Cuenta: ${t.cuentaIban}`));
            console.log(chalk.white(`    Fecha: ${t.fecha}`));
            console.log(chalk.white(`    Tipo: ${t.tipo}`));
            console.log(chalk.white(`    Intento: ${t.intentos} de ${MAX_INTENTOS}`));
        });
    }
    
    readlineSync.question(chalk.blue('\nPresiona Enter para volver al menú...'));
    mostrarMenu();
}

// Procesar transacciones pendientes
function procesarTransacciones() {
    console.clear();
    console.log(chalk.blue.bold('=== PROCESAR TRANSACCIONES ==='));
    
    const pendientes = transacciones.filter(t => t.estado === 'PEN' && !t.procesada);
    
    if (pendientes.length === 0) {
        console.log(chalk.yellow('No hay transacciones pendientes para procesar'));
        readlineSync.question(chalk.blue('\nPresiona Enter para volver al menú...'));
        mostrarMenu();
        return;
    }
    
    // Mostrar una transacción a la vez para procesarla
    let index = 0;
    
    function procesarSiguiente() {
        if (index >= pendientes.length) {
            console.log(chalk.green('Todas las transacciones han sido procesadas'));
            readlineSync.question(chalk.blue('\nPresiona Enter para volver al menú...'));
            mostrarMenu();
            return;
        }
        
        const t = pendientes[index];
        console.clear();
        console.log(chalk.green(`\n[${index + 1}/${pendientes.length}] Transacción: ${t.codTransaccion}`));
        console.log(chalk.white(`    Monto: ${t.monto} ${t.moneda}`));
        console.log(chalk.white(`    Cuenta: ${t.cuentaIban}`));
        console.log(chalk.white(`    Fecha: ${t.fecha}`));
        console.log(chalk.white(`    Tipo: ${t.tipo}`));
        console.log(chalk.white(`    Intento: ${t.intentos} de ${MAX_INTENTOS}`));
        
        const options = ['Aprobar', 'Rechazar', 'Saltar'];
        const seleccion = readlineSync.keyInSelect(options, '¿Qué deseas hacer con esta transacción?');
        
        // Encontrar el índice real en el array de transacciones
        const transaccionRealIndex = transacciones.findIndex(tr => tr.codTransaccion === t.codTransaccion);
        
        switch (seleccion) {
            case 0: // Aprobar
                // Actualizar la transacción como aprobada
                transacciones[transaccionRealIndex].estado = 'APR';
                transacciones[transaccionRealIndex].procesada = true;
                transacciones[transaccionRealIndex].fechaProcesamiento = new Date().toISOString();
                console.log(chalk.green('✓ Transacción aprobada'));
                guardarTransacciones();
                break;
            case 1: // Rechazar
                // Verificar si ha alcanzado el máximo de intentos
                if (t.intentos >= MAX_INTENTOS) {
                    transacciones[transaccionRealIndex].estado = 'REC';
                    transacciones[transaccionRealIndex].procesada = true;
                    transacciones[transaccionRealIndex].fechaProcesamiento = new Date().toISOString();
                    console.log(chalk.red('✗ Transacción rechazada definitivamente (máximo de intentos alcanzado)'));
                } else {
                    transacciones[transaccionRealIndex].estado = 'REC-TEMP';
                    console.log(chalk.yellow('⚠ Transacción rechazada temporalmente. Se reintentará al día siguiente.'));
                }
                guardarTransacciones();
                break;
            case 2: // Saltar
                console.log(chalk.blue('Transacción saltada'));
                break;
            default:
                console.log(chalk.yellow('Operación cancelada'));
                mostrarMenu();
                return;
        }
        
        // Pasar a la siguiente transacción
        index++;
        setTimeout(procesarSiguiente, 500);
    }
    
    procesarSiguiente();
}

// Ver historial de transacciones
function verHistorialTransacciones() {
    console.clear();
    console.log(chalk.blue.bold('=== HISTORIAL DE TRANSACCIONES ==='));
    
    if (transacciones.length === 0) {
        console.log(chalk.yellow('No hay transacciones en el historial'));
    } else {
        // Agrupar por estado
        const aprobadas = transacciones.filter(t => t.estado === 'APR');
        const rechazadas = transacciones.filter(t => t.estado === 'REC');
        const pendientes = transacciones.filter(t => t.estado === 'PEN' && !t.procesada);
        const rechazadasTemp = transacciones.filter(t => t.estado === 'REC-TEMP');
        
        console.log(chalk.green(`Aprobadas: ${aprobadas.length}`));
        console.log(chalk.red(`Rechazadas: ${rechazadas.length}`));
        console.log(chalk.yellow(`Pendientes: ${pendientes.length}`));
        console.log(chalk.yellow(`Rechazadas temporalmente: ${rechazadasTemp.length}`));
        
        console.log(chalk.white.bold('\nÚltimas 5 transacciones:'));
        const ultimas = [...transacciones].reverse().slice(0, 5);
        
        ultimas.forEach(t => {
            const estadoColor = t.estado === 'APR' ? chalk.green :
                                t.estado === 'REC' ? chalk.red :
                                t.estado === 'REC-TEMP' ? chalk.yellow : chalk.blue;
                                
            console.log(estadoColor(`[${t.estado}] ${t.codTransaccion} - ${t.monto} ${t.moneda} - Intentos: ${t.intentos}`));
        });
    }
    
    readlineSync.question(chalk.blue('\nPresiona Enter para volver al menú...'));
    mostrarMenu();
}

// Limpiar todas las transacciones
function limpiarTransacciones() {
    console.clear();
    console.log(chalk.red.bold('=== LIMPIAR TRANSACCIONES ==='));
    
    const confirmacion = readlineSync.keyInYN(chalk.red('¿Estás seguro de que quieres eliminar TODAS las transacciones?'));
    
    if (confirmacion) {
        transacciones = [];
        guardarTransacciones();
        console.log(chalk.green('Todas las transacciones han sido eliminadas'));
    } else {
        console.log(chalk.blue('Operación cancelada'));
    }
    
    readlineSync.question(chalk.blue('\nPresiona Enter para volver al menú...'));
    mostrarMenu();
}

// Manejar cierre del servidor
process.on('SIGINT', () => {
    // Detener el modo automático si está activo
    if (modoAutomaticoActivo && intervaloPruebas) {
        console.log(chalk.yellow('\nDeteniendo modo automático...'));
        clearInterval(intervaloPruebas);
        modoAutomaticoActivo = false;
    }
    
    console.log(chalk.yellow('\nGuardando transacciones...'));
    guardarTransacciones();
    console.log(chalk.green('¡Hasta pronto!'));
    process.exit(0);
}); 