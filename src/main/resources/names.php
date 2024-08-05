<?php

$uniq = [];
$json = json_decode(file_get_contents('chunks.json'), true);
//foreach($json as $k1 => $v1) {
	foreach($json as $k2 => $v2) {
		foreach($v2 as $k => $v) {
			if($k == "Sections") {
				foreach($v as $k3 => $v3) {
					foreach($v3 as $k4 => $v4) {
						if($k4 == "Quest") {
							foreach($v4 as $k5 => $v5) {
								echo $v5 . "\n";
							}
						}
					}
				}
			}
			/*if(!$uniq[$k]) {
				$uniq[$k] = $v;
			}*/
		}
	}
//}

var_dump($uniq);