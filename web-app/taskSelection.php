<?php
$usr=$_GET["usr"];
?>
<p>This is task <?=$usr?>.</p>
<p>Request received on: 

<?php
print date("l M dS, Y, H:i:s");
?>
</p>