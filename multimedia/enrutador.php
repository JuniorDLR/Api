<?php


require_once 'conexion.php';

// Obtener el número total de imágenes disponibles
$sql = "SELECT COUNT(*) AS total FROM recursos";
$resultado = $mysql->query($sql);
$row = $resultado->fetch_assoc();
$total_imagenes = $row["total"];

// Generar un número aleatorio en el rango de las imágenes disponibles
$indice_imagen = rand(0, $total_imagenes - 1);

$response = array();

$sql = "SELECT * FROM recursos LIMIT $indice_imagen, 1";
$resultado = $mysql->query($sql);

while($row = $resultado->fetch_assoc()) {
    $imagen = $row["tipo"];
 


    $response['message'] = base64_encode($imagen);
}

$mysql->close();

echo json_encode($response);



?>
