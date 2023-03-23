<?php


$mysql = new mysqli("localhost","root","","enrutador");

if($mysql->connect_error){

    echo 'Error de conexion';
}




?>