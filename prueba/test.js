const http = require('http');

// Transacción de prueba
const transaccionPrueba = {
    codTransaccion: 'TEST' + Math.floor(Math.random() * 10000),
    tipo: 'REC',
    marca: 'VISA',
    monto: 150.75,
    codigoUnicoTransaccion: 'UNIQ' + Date.now(),
    fecha: new Date().toISOString(),
    estado: 'PEN',
    moneda: 'USD',
    pais: 'EC',
    tarjeta: 4532123456789012,
    fechaCaducidad: '2025-12-31',
    swiftBanco: 'PICHECEQ',
    cuentaIban: 'EC012345678901234567890',
    diferido: false
};

// Opciones para la solicitud HTTP
const options = {
    hostname: 'localhost',
    port: 8082,
    path: '/api/v1/transacciones',
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
        'Content-Length': Buffer.byteLength(JSON.stringify(transaccionPrueba))
    }
};

// Realizar la solicitud HTTP
const req = http.request(options, (res) => {
    console.log(`ESTADO: ${res.statusCode}`);
    
    res.setEncoding('utf8');
    let responseData = '';
    
    res.on('data', (chunk) => {
        responseData += chunk;
    });
    
    res.on('end', () => {
        console.log('RESPUESTA:');
        console.log(responseData);
        
        if (res.statusCode === 200) {
            console.log('\n✅ Transacción de prueba enviada exitosamente');
            console.log('Ahora puedes procesarla en el simulador');
        } else {
            console.log('\n❌ Error al enviar la transacción de prueba');
        }
    });
});

req.on('error', (e) => {
    console.error(`❌ Problema con la solicitud: ${e.message}`);
    console.log('¿Está el servidor del simulador en ejecución?');
});

// Enviar la transacción
console.log('Enviando transacción de prueba...');
req.write(JSON.stringify(transaccionPrueba));
req.end(); 