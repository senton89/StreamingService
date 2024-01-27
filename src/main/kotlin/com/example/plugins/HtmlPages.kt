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
        <a href="https://github.com/yourusername" target="_blank" style="color: white;"><img src = "file:///C:/Users/sento/Downloads/github-pages-logo-repository-fork-github-86eddab19cbc3ae293ada0fe0fb9e27d.png" height = "35"></img></a>
        <br>
        <a href="https://t.me/yourusername" target="_blank" style="color: white;"><img src = "C:\Users\sento\Downloads\telegram-icon-2048x2048-oont1oj1.png" height = "35"></img></a>
    </div>
</div>
<div class = "form">
    <form action="/view-video" method="get" enctype="multipart/form-data">
        <label for="fileUpload">Выберите видеофайл для загрузки:</label>
        <input type="file" id="fileUpload" name="fileUpload" accept="video/*">
        <input type="submit" value="Загрузить">
    </form>
</div>
</body>
</html>
""")

val video_form = ("""
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>Video Stream</title>
                        <style>
                        body {
                        background-color: #000024;
                        }
                        </style>
                    </head>
                    <body>
                        <video width="1900" height="900" controls>
                            <source src="/upload" type="video/mp4">
                            Ваш браузер не поддерживает элемент <code>video</code>.
                        </video>
                    </body>
                    </html>
                """)