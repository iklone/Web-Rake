<?php
	header("Content-type:application/x-www-form-urlencoded;charset=utf-8");
    include "db.php";
	$taskID = $_POST["currentTaskID"];
	
	// find the result
	//SELECT resultTime, resultValue FROM Result WHERE ScrapeID = 85 order by resultTime DESC
	$results = mysqli_query($link, "select scrapeName, scrapeID, sampleData from Scrape where taskID ="."'"."$taskID"."'");

	if(mysqli_num_rows($results) >= 1){
		$scrapeList = [];
		while($scrapeInfo = mysqli_fetch_assoc($results)) {
			$id = $scrapeInfo['scrapeID'];
			$values = mysqli_query($link, "SELECT b.scrapeName, a.resultValue FROM Result a left join Scrape b on a.scrapeID = b.scrapeID WHERE a.ScrapeID = "."'"."$id"."'"." order by resultTime DESC");
			if(mysqli_num_rows($values) >= 1){
				$value = mysqli_fetch_assoc($values);
			}else{
				$value = $scrapeInfo;
			}
	    	$scrapeList[] = $value;
		}
		//send all task names back
		echo json_encode($scrapeList);
	}else{
		echo "no record";
	}


 	mysqli_close($link);//close the link
?>