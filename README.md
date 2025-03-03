# Java_Servlets

Practice project in Training period

#First week
# Client (Post Manager)

Post Manager is the web application for managing posts, allowing you to add, update, delete, and search posts by title or content. The backend is assumed to be a RESTful API running at `http://localhost:8080/Java_Servlets_Servers/posts`.

- **Add Post**: Allows you to add a new post by specifying the title and content.
- **Update Post**: Allows you to update the content of an existing post by title.
- **Delete Post**: Allows you to delete a post by title.
- **Search by Title**: Allows you to search posts by their title.
- **Search by Content**: Allows you to search posts by their body/content.

## Technologies Used

- **HTML**: Used for the structure of the web pages.
- **CSS**: Used for basic styling.
- **JavaScript**: For handling asynchronous operations (fetch API) and manipulating DOM.
- **RESTful API**: The backend is assumed to be running on `localhost:8080` for CRUD operations on the posts.

## Requirements

To run this application:
1. Make sure the backend API is running at `http://localhost:8080/Java_Servlets_Servers/posts`.
2. Open `index.html` in a web browser to interact with the Post Manager.

### HTML Structure
The HTML structure contains:
- A header with the title `Post Manager`.
- Forms for adding, updating, deleting, and searching posts.
- Two tables:
  - One to display all posts (`postsTable`).
  - One to display search results (`searchTable`).

#### 1. **API_URL**
   - The `API_URL` variable is the endpoint for the backend API that serves the posts.
   
   ```javascript
   const API_URL = "http://localhost:8080/Java_Servlets_Servers/posts";
```
# Server (Post Service API)

A simple RESTful API for managing blog posts. You can create, read, update, delete, and search blog posts by title or content. The backend is built with Java Servlets, and it uses a MySQL database for storing posts.
## Features

  - **Create:** Add a new post with a title and body.
  - **Read:** Retrieve all posts or search posts by title or content.
  - **Update:** Modify the content of an existing post by title.
  - **Delete**: Remove a post by its title.
  - **Search:** Find posts by title or body content.

## Setup

- **Clone the repo:**
    ```javascript
    git clone https://github.com/Pradeesh-Saravanan/Java_Servlets.git```
- **Set up MySQL:**
        Create a database demo and a posts table.
        Update database credentials in the code.
- **Deploy to Tomcat:**
        Deploy the app in a servlet container like Tomcat.
        Configure the servlet in web.xml.

## Endpoints
- **GET /posts:** Get all posts.
- **GET /posts/title?key=your_keyword:** Search posts by title.
- **GET /posts/content?key=your_keyword:** Search posts by content.
- **POST /posts:** Create a new post.
- **PUT /posts:** Update an existing post by title.
- **DELETE /posts:** Delete a post by title.

# 24/02/2025

Features added 
- **User registration:** signup.html
- **User login page:** login.html
- **Users table:** database table for userLogin 
- **Dynamic data for users:** filter data by user created

Issues resolved

- **Direct dashboard access:** using localStorage
- **Lazy login:** accessing login after login successsful redirects to dashboard.html using localStorage
- **Plain passwords in database:** used BCrypt[ hashpw(), getsalt(), checkpw() ] in servlets 
- **Avoid direct links to file in browsers:** used live server and relative mapping of html pages

# 25/02/2025

Features added and learned: 
- **Backend authentication:** login.html
- **Servlet Filters** 
- **OWASP TOp 10** 
- **Session and Cookies:** filter data by user created

# 28/02/2025

Issues faced:

- **Security (OWASP)**
- **Unauthorized access and update of data**
- **Deceived request**

Changes made:

- **Session management:** moved to Servlet using Cookies
- **User Interface:** Popup form creation, Using same table to display search results
- **Easy usage:** Dedicated buttons for update and delete posts
- **Lazy login:** Easy login after authenticated using cookie id
- **Database schema:** userLogin(username,password,id,value) where UUID and boolean for value is used
