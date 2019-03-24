<head>
    <title>Web Scrape</title>
    <link rel = "stylesheet" type = "text/css" href = "main.css">
    <script>
        function deleteVerify() {
            var result = confirm("Are you sure you want to delete this task?");
            if (result == true) {

            } else {

            }
        }
		function insertTaskListing(name) {
			var article = document.createElement("article");

			var title = document.createElement("span");
			title.innerHTML = name;
			article.appendChild(title);

			var meta = document.createElement("h6");
			meta.innerHTML = "TODO";
			article.appendChild(meta);

			document.getElementById("taskListing").appendChild(article);
		}
    </script>
</head>

<body>
    <h1 onclick="window.location.href='./main.php'">Web Scrape</h1>

    <span class="leftTopBar">
        <form>
            <input type="text" placeholder="Search tasks...">
            <button type="button">Search</button>
        </form>
    </span>

    <span class="rightTopBar">
        <i>Logged in as psyjct</i>
    </span>

    <div id="taskListing" class="split left" style="overflow: scroll;">
        <article class="newTask">
            +New Task
        </article>
        <article>
            <span class="taskName">Task A</span><span class="taskMeta">Next: 10:50</span>
        </article>
		
		<script>
		<?php
			include 'db.php';
			$taskData = mysqli_query($conn, 'SELECT * FROM Task;');
			foreach ($taskData as $task){
				echo 'insertTaskListing("' . $task["taskName"] . '");';
			}
		?>
		</script>
		
        <div style="margin: 100px;"></div>
    </div>

    <div class="split right">
        <div style="margin:15px">
            <span class="selectedTaskName">Task A</span>
            <span class="rightButtons">
                <a href="./editTask.php"><button>Edit</button></a>
                <button onclick="deleteVerify()">Delete</button>
            </span>
            <p>Description</p>
            Access by:
            <ul>
                <li>user</li>
                <li>group</li>
            </ul>
            <div class="taskURL"><a href="http://example.com">http://example.com</a></div>
            <ul>
                <li>Page Title</li>
                <li>Value</li>
            </ul>

            <table>
                <tr>
                    <th>#</th>
                    <th>Name</th>
                    <th>Value</th>
                    <th>Timestamp</th>
                </tr>
                <tr>
                    <td>0</td>
                    <td>Page Title</td>
                    <td>"Hello World"</td>
                    <td>0900</td>
                </tr>
                <tr>
                    <td>1</td>
                    <td>Value</td>
                    <td>110</td>
                    <td>0900</td>
                </tr>
            </table>
        </div>
    </div>
</body>