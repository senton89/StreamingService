package com.example.plugins


val main_form = ("""
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Стрим</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" integrity="sha512-lQP1BiSutAy+g9GF+TCuK5B1uSm9oyB2z3iRWk1ov8P9TTx+lN4D/8FE3vU5S0qQy7/DTcepqWtvR5+X/dn4vg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <style>
        body {
            margin: 0;
            padding: 0;
            font-family: Arial, sans-serif;
            background-color: #000024;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            color: white;
            text-align: center;
            font-size: 70px;
        }
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            width: 100%;
        }
        .header .title {
            flex-grow: 1;
        }
        .social-icons {
            font-size: 24px;
        }
        .social-icons i {
            margin-left: 10px;
        }
        .form{
            font-size: 14px;
            position: absolute;
            bottom: 0;
        }
    </style>
</head>
<body>
<div class="header">
    <div class="title">
        <h1>Welcome to<br>my stream</h1>
    </div>
    <div class="social-icons">
        <a href="https://github.com/senton89" target="_blank" style="color: white;">
        <img src = "/static/github-logo.png" height = "35">
        </img>
        </a>  
    </div>
</div>
<div class = "form">
    <form id="submit-form" action="/upload" method="post" enctype="multipart/form-data">
        <label for="fileUpload">Выберите видеофайл для загрузки:</label>
        <input type="file" id="fileUpload" name="fileUpload" accept="video/*">
        <input type="submit" value="Загрузить">
    </form>
</div>
</body>
<script>
document.getElementById('submit-form').onsubmit = function() {
    fetch('/upload', {
        method: 'POST',
        body: new FormData(this),
    }).then(() => {
        window.location.href = '/view-video/';
    }).catch((error) => {
        console.error('Error:', error);
    });
    return false;
};
</script>
</html>
""")



val video_form = ("""
                    <!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Video Stream</title>
    <!-- Include the Video.js library CSS -->
    <link href="https://unpkg.com/video.js/dist/video-js.css" rel="stylesheet">
    <style>
        body {
            background-color: #000024;
        }
        .video-js {
            width: 100%;
            height: auto;
        }
    </style>
</head>
<body>
    <!-- Setup the video player with Video.js -->
    <video 
        id="my_video_1" 
        class="video-js vjs-default-skin" 
        controls 
        preload="auto" 
        width="1900" 
        height="900" 
        data-setup='{}'>
        <source src="/upload" type="video/mp4">
        Ваш браузер не поддерживает элемент <code>video</code>.
    </video>
    <script src="https://unpkg.com/video.js/dist/video.js"></script>
    <script src="https://unpkg.com/videojs-http-streaming/dist/videojs-http-streaming.js"></script>
    <script>
        (function() {
            videojs(document.querySelector('#my_video_1'));
        })();
    </script>
</body>
</html>

                """)