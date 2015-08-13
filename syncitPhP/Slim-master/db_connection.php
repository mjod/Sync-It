<?php
function getDB(){
	$dbhost="localhost";
	$dbuser="root";
	$dbpass="";
	$dbname="syncitdb";

	
	// Create a DB connection
	$conn = new mysqli($dbhost, $dbuser, $dbpass, $dbname);
	if ($conn->connect_error) {
		die("connection failed " . $conn->connection_error . "\n");
	}
	
	
	return $conn;
}
?>