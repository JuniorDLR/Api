<?php
require_once 'conexion2.php';
// Obtener los archivos enviados
$archivos = $_FILES['file'];



// Recorrer los archivos y guardarlos en la base de datos
foreach ($archivos['tmp_name'] as $key => $tmp_name) {
    $nombre_archivo = $archivos['name'][$key];
    $tipo_archivo = $archivos['type'][$key];
    $tamanio_archivo = $archivos['size'][$key];
    $ruta_archivo = 'ruta/al/archivo/'.$nombre_archivo; // Cambiar por la ruta real donde se van a guardar los archivos
    move_uploaded_file($tmp_name, $ruta_archivo);

    $stmt = $mysql->prepare('INSERT INTO archivos (nombre, tipo, tamano, ruta) VALUES (?, ?, ?, ?)');
    $stmt->bind_param('ssis', $nombre_archivo, $tipo_archivo, $tamanio_archivo, $ruta_archivo);
    $stmt->execute();
    $stmt->close();
}

// Cerrar la conexión
$mysql->close();
?>

