<?php

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    require_once 'conexion.php';

    // Obtener los datos del archivo
    $imageData = file_get_contents($_FILES['file']['tmp_name']);
    $Nombre = $_FILES['file']['name'];
    $Tipo = $_FILES['file']['type'];
    $Tamaño = $_FILES['file']['size'];
    $Ruta = 'servidor/' . $Nombre;

    // Mover el archivo a la ruta de almacenamiento
    move_uploaded_file($_FILES['file']['tmp_name'], $Ruta);

    // Insertar los datos en la base de datos
    $stmt = $mysql->prepare("INSERT INTO archivos (nombre, tipo, tamano, ruta) VALUES (?,?,?,?)");
    $stmt->bind_Param('ssss' ,$Nombre,$Tipo,$Tamaño,$Ruta);
    $stmt->execute();

    if($stmt == true){
        echo 'Datos guardados exitosamente';
    } else {
        echo 'Error al conectar en la base de datos';
    }
} 








?>
