<?php


$mysql = new mysqli("localhost","root","","multimedia");

if($mysql->connect_error){

    echo 'Error de conexion';
}




?>