<?php
/**
 * Step 1: Require the Slim Framework
 *
 * If you are not using Composer, you need to require the
 * Slim Framework and register its PSR-0 autoloader.
 *
 * If you are using Composer, you can skip this step.
 */
require 'Slim/Slim.php';

\Slim\Slim::registerAutoloader();

/**
 * Step 2: Instantiate a Slim application
 *
 * This example instantiates a Slim application using
 * its default settings. However, you will usually configure
 * your Slim application now by passing an associative array
 * of setting names and values into the application constructor.
 */
$app = new \Slim\Slim();

/**
 * Step 3: Define the Slim application routes
 *
 * Here we define several Slim application routes that respond
 * to appropriate HTTP request methods. In this example, the second
 * argument for `Slim::get`, `Slim::post`, `Slim::put`, `Slim::patch`, and `Slim::delete`
 * is an anonymous function.
 */

// GET route
require_once __DIR__ . '/db_connection.php';


$app->get(
    '/',
    function () {
		echo "<h1>Matt O'Donnell Site is up and running<h1>";;
    }
);

$app->get(
    '/user/:mid',
    function ($mid) {
		$conn = getDB();
		
		if ($stmt = $conn->prepare("SELECT * FROM userinfo WHERE username = ?")){
		
			$stmt->bind_param("s", $mid);
			
			$stmt->execute();
			
			$res = $stmt->get_result();
			
			$return_arr = array();
			
			while ($row = $res->fetch_assoc()){
				array_push($return_arr,$row);
			}
			
			$stmt->close();
			echo json_encode($return_arr);
			$conn->close();
		}
    }
);

$app->get(
    '/createuser/:user/:pass/:name',
    function ($user, $pass, $name) {
		$conn = getDB();
		$return_arr = array();

		if ($stmt2 = $conn->prepare("SELECT * FROM userinfo WHERE username = ?")){
		
			$stmt2->bind_param("s", $user);
			
			$stmt2->execute();
			
			$res = $stmt2->get_result();
						
			while ($row = $res->fetch_assoc()){
				array_push($return_arr,$row);
			}
			
			$stmt2->close();
			echo json_encode($return_arr);
		}
		if (sizeof($return_arr) == 0){
			if ($stmt = $conn->prepare("INSERT INTO userinfo VALUES(?,?,?,'','','1')")){
			
				$stmt->bind_param("sss", $user,$pass,$name);
				
				$stmt->execute();
				
				$res = $stmt->get_result();
				
				
				//while ($row = $res->fetch_assoc()){
				//	array_push($return_arr,$row);
				//}
				
				$stmt->close();			
			}
		}
		$conn->close();

    }
);

$app->get(
    '/familyuser/:mid/:username',
    function ($mid,$user) {
		$conn = getDB();
	
		if ($stmt = $conn->prepare("SELECT * FROM familyuserinfo WHERE family_username = ?")){
		
			$stmt->bind_param("s", $mid);
			
			$stmt->execute();
			
			$res = $stmt->get_result();
			
			$return_arr = array();
			
			while ($row = $res->fetch_assoc()){
				array_push($return_arr,$row);
			}
			
			$stmt->close();
			
			echo json_encode($return_arr);
		}
		if(sizeof($return_arr)==1){
			if ($stmt1 = $conn->prepare("UPDATE userinfo SET familyname=? WHERE username=?")){		
				$stmt1->bind_param("ss", $mid,$user);
				
				$stmt1->execute();
				
				$res1 = $stmt1->get_result();
				
				$stmt1->close();
			}
		}
		$conn->close();

    }
);

$app->get(
    '/createfamily/:familyuser/:familypass/:familyname/:username',
    function ($famuser, $fampass, $famname,$user) {
		$conn = getDB();
		if ($stmt = $conn->prepare("SELECT * FROM familyuserinfo WHERE family_username = ?")){
		
			$stmt->bind_param("s", $famuser);
			
			$stmt->execute();
			
			$res = $stmt->get_result();
			
			$return_arr = array();
			
			while ($row = $res->fetch_assoc()){
				array_push($return_arr,$row);
			}
			
			$stmt->close();
			
			echo json_encode($return_arr);
		}
		if(sizeof($return_arr) == 0){		
			if ($stmt = $conn->prepare("INSERT INTO familyuserinfo VALUES(?,?,?,'')")){
			
				$stmt->bind_param("sss", $famuser,$fampass,$famname);
				
				$stmt->execute();
				
				$res = $stmt->get_result();
				
				
				//while ($row = $res->fetch_assoc()){
				//	array_push($return_arr,$row);
				//}
				
				$stmt->close();			
			}
			
			if ($stmt1 = $conn->prepare("UPDATE userinfo SET familyname=? WHERE username=?")){
			
				$stmt1->bind_param("ss", $famuser,$user);
				
				$stmt1->execute();
								
				$stmt1->close();			
			}
		}
		$conn->close();

    }
);

$app->get(
    '/getfamily/:mid',
    function ($mid) {
		$conn = getDB();
		
		if ($stmt = $conn->prepare("SELECT name FROM userinfo WHERE familyname = ?")){
		
			$stmt->bind_param("s", $mid);
			
			$stmt->execute();
			
			$res = $stmt->get_result();
			
			$return_arr = array();
			
			while ($row = $res->fetch_assoc()){
				array_push($return_arr,$row);
			}
			
			$stmt->close();
			echo json_encode($return_arr);
			$conn->close();
		}
    }
);

$app->get(
    '/updatefamilyphoto/:family_username/:photo',
    function ($family_username,$photo) {
		$conn = getDB();
		$eventData = array();
		if ($stmt = $conn->prepare("UPDATE familyuserinfo SET family_picture=? WHERE family_username=?")){
		
			$stmt->bind_param("ss", $photo,$family_username);
			
			$stmt->execute();
			
			$stmt->close();
		}
		$conn->close();
	}
);

$app->get(
    '/getfamilyphoto/:family_username',
    function ($family_username) {
		$conn = getDB();
		
		if ($stmt = $conn->prepare("SELECT family_picture FROM familyuserinfo WHERE family_username = ?")){
		
			$stmt->bind_param("s", $family_username);
			
			$stmt->execute();
			
			$res = $stmt->get_result();
			
			$return_arr = array();
			
			while ($row = $res->fetch_assoc()){
				array_push($return_arr,$row);
			}
			
			$stmt->close();
			echo json_encode($return_arr);
			$conn->close();
		}
    }
);

$app->get(
    '/updateevent/:name/:description/:start/:end/:location/:guests/:createdby/:family_username/:event_id',
    function ($name, $description, $start, $end, $location, $guests, $createdby,$family_username,$event_id) {
		$conn = getDB();
		$eventData = array();
		if ($stmt = $conn->prepare("UPDATE familyevents SET name=?,description=?,start=?,end=?,location=?,guests=?,createdby=?,family_username=? WHERE event_id=?")){
		
			$stmt->bind_param("sssssssss", $name, $description, $start, $end, $location, $guests, $createdby,$family_username,$event_id);
			
			$stmt->execute();
			
			$stmt->close();
		}
		$conn->close();
	}
);

$app->get(
    '/createevent/:name/:description/:start/:end/:location/:guests/:createdby/:family_username',
    function ($name, $description, $start, $end, $location, $guests, $createdby,$family_username) {
		$conn = getDB();
		$eventData = array();
		if ($stmt = $conn->prepare("INSERT INTO familyevents (name,description,start,end,location,guests,createdby,family_username) VALUES(?,?,?,?,?,?,?,?)")){
		
			$stmt->bind_param("ssssssss", $name, $description, $start, $end, $location, $guests, $createdby,$family_username);
			
			$stmt->execute();
			
			$stmt->close();
		}
		if ($stmt = $conn->prepare("SELECT * FROM familyevents WHERE name=? AND description=? AND start=? AND end=? AND location=? AND guests=? AND createdby=? AND family_username=?")){
		
			$stmt->bind_param("ssssssss", $name, $description, $start, $end, $location, $guests, $createdby,$family_username);
			
			$stmt->execute();
			
			$res = $stmt->get_result();
				
			while ($row = $res->fetch_assoc()){
				array_push($eventData,$row);
			}
			$eventData = json_encode($eventData);
			echo $eventData;
			
			$stmt->close();
		}
		
		$conn->close();
		
		$con = mysql_connect("localhost", "root","");
		   if(!$con){
			die('MySQL connection failed');
		   }
		 
		   $db = mysql_select_db("syncitdb");
		   if(!$db){
			die('Database selection failed');
		   }
		$registatoin_ids = array();
		$sql = "SELECT *FROM userinfo WHERE familyname='".$family_username."'";
		$result = mysql_query($sql, $con);
		while($row = mysql_fetch_assoc($result)){
			array_push($registatoin_ids, $row['regid']);
		}

		     $url = 'https://android.googleapis.com/gcm/send';
   
		$message = array("Notice" => $eventData);
         $fields = array(
             'registration_ids' => $registatoin_ids,
             'data' => array("message" => $eventData),
         );
		 print_r($fields);
   
         $headers = array(
             'Authorization: key=AIzaSyAMYQLfBt5Ttm7WVeLvwuG2ZcoeFrwd5pU',
             'Content-Type: application/json'
         );
         // Open connection
         $ch = curl_init();
   
         // Set the url, number of POST vars, POST data
         curl_setopt($ch, CURLOPT_URL, $url);
   
         curl_setopt($ch, CURLOPT_POST, true);
         curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
         curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
   
         // Disabling SSL Certificate support temporarly
         curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
   
         curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
   
         // Execute post
         $result = curl_exec($ch);
         if ($result === FALSE) {
             die('Curl failed: ' . curl_error($ch));
         }
   
         // Close connection
         curl_close($ch);
         echo $result;
		
		
    }
);



$app->get(
    '/notifyparents/:family_username/:name/:eventname/:dist/:lat/:lon',
    function ($family_username, $name, $eventname, $dist, $lat, $lon) {

		
		$con = mysql_connect("localhost", "root","");
		   if(!$con){
			die('MySQL connection failed');
		   }
		 
		   $db = mysql_select_db("syncitdb");
		   if(!$db){
			die('Database selection failed');
		   }
		$registatoin_ids = array();
		$sql = "SELECT *FROM userinfo WHERE parent='1' AND familyname='" . $family_username . "'";
		$result = mysql_query($sql, $con);
		while($row = mysql_fetch_assoc($result)){
			array_push($registatoin_ids, $row['regid']);
		}

		     $url = 'https://android.googleapis.com/gcm/send';
		
		$notifcation = $name . " is currently " . $dist . " miles away from " . $eventname . "|". $lat . ",".$lon;
		$fields = array(
             'registration_ids' => $registatoin_ids,
             'data' => array("message" => $notifcation),
         );
		 print_r($fields);
   
         $headers = array(
             'Authorization: key=AIzaSyAMYQLfBt5Ttm7WVeLvwuG2ZcoeFrwd5pU',
             'Content-Type: application/json'
         );
         // Open connection
         $ch = curl_init();
   
         // Set the url, number of POST vars, POST data
         curl_setopt($ch, CURLOPT_URL, $url);
   
         curl_setopt($ch, CURLOPT_POST, true);
         curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
         curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
   
         // Disabling SSL Certificate support temporarly
         curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
   
         curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
   
         // Execute post
         $result = curl_exec($ch);
         if ($result === FALSE) {
             die('Curl failed: ' . curl_error($ch));
         }
   
         // Close connection
         curl_close($ch);
         echo $result;		
    }
);





$app->get(
    '/getfamilyevents/:familyusername',
    function ($familyusername) {
		$conn = getDB();
		
		if ($stmt = $conn->prepare("SELECT * FROM familyevents WHERE family_username = ?")){
		
			$stmt->bind_param("s", $familyusername);
			
			$stmt->execute();
			
			$res = $stmt->get_result();
			
			$return_arr = array();
			
			while ($row = $res->fetch_assoc()){
				array_push($return_arr,$row);
			}
			
			$stmt->close();
			echo json_encode($return_arr);
			$conn->close();
		}
    }
);

$app->get(
    '/getfamilymembers/:familyusername',
    function ($familyusername) {
		$conn = getDB();
		
		if ($stmt = $conn->prepare("SELECT username, name, parent FROM userinfo WHERE familyname=?")){
		
			$stmt->bind_param("s", $familyusername);
			
			$stmt->execute();
			
			$res = $stmt->get_result();
			
			$return_arr = array();
			
			while ($row = $res->fetch_assoc()){
				array_push($return_arr,$row);
			}
			
			$stmt->close();
			echo json_encode($return_arr);
			$conn->close();
		}
    }
);

$app->get(
    '/updateregid/:username/:regid',
    function ($username, $regid) {
		$conn = getDB();
		
		if ($stmt = $conn->prepare("UPDATE userinfo SET regid=? WHERE username=?")){		
				$stmt->bind_param("ss", $regid,$username);
				
				$stmt->execute();
								
				$stmt->close();
		}
		
		$conn->close();
		
    }
);
$app->get(
    '/updateparentalcontrol/:username/:parent',
    function ($username, $parent) {
		$conn = getDB();
		
		if ($stmt = $conn->prepare("UPDATE userinfo SET parent=? WHERE username=?")){		
				$stmt->bind_param("is", $parent,$username);
				
				$stmt->execute();
								
				$stmt->close();
		}
		
		$conn->close();
		
    }
);





//--------------------------------------------------------------------------------------------------------------

// POST route
$app->post(
    '/post',
    function () {
		echo 'This is a POST route';
    }
);

// PUT route
$app->put(
    '/put',
    function () {
        echo 'This is a PUT route';
    }
);

// PATCH route
$app->patch('/patch', function () {
    echo 'This is a PATCH route';
});

// DELETE route
$app->delete(
    '/delete',
    function () {
        echo 'This is a DELETE route';
    }
);

/**
 * Step 4: Run the Slim application
 *
 * This method should be called last. This executes the Slim application
 * and returns the HTTP response to the HTTP client.
 */
$app->run();
