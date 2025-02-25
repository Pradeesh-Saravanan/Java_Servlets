// const apiUrl = "https://crudcrud.com/api/f98b070bb244416dbca02d2b15591c0b/posts";
const apiUrl = "http://localhost:8080/Server/posts";
function createPost(){
    const post = {
        title:document.getElementById('id').value,
        body:document.getElementById('content').value,
    };

    fetch(apiUrl,
        {
            method:'POST',
            headers:{
                'Content-Type':'application/json'
            },
            body:JSON.stringify(post)
        }
    ).then(response=>response.json());

    document.getElementById('id').value='';
    document.getElementById('content').value='';
}

function getPosts(){
    fetch(apiUrl).then(response=>response.json())
    .then(data=>
    {
        const postsTable = document.getElementById('postsTable').getElementsByTagName('tbody')[0];
        postsTable.innerHTML="";

        data.forEach(element => {
            const row = postsTable.insertRow();
            row.insertCell(0).textContent=element.title;
            row.insertCell(1).textContent=element.body;

            const actionsCell = row.insertCell(2);
            actionsCell.innerHTML =`<button onClick="show('${element.id}','${element.title}','${element.body}')" style="background-color: bisque;border-radius: 10px;">Show</button>
                                    <button onClick="deletePost('${element.id}')" style="background-color: lavender;border-radius: 10px;">Delete</button>`;
        });
    }
    ).catch(error => console.error("Error:", error));
    document.getElementById('id').value='';
    document.getElementById('content').value='';
}

function show(postId,title,content){
    document.getElementById('id').value = title;
    document.getElementById('content').value = content;
    document.getElementById('updateButton').onclick = function(){
        updatePost(postId);
    };
}
function updatePost(postId){
    const post = {
        // _id:postId,
        title:document.getElementById('id').value,
        body :document.getElementById('content').value
    };
    if(post.title && post.body){
        fetch(`${apiUrl}/${postId}`,
        {
            method:'PUT',
            headers:
            {
                'Content-Type':'application/json'
            },
            body:JSON.stringify(post)
        });
    }
    document.getElementById('id').value='';
    document.getElementById('content').value='';
}

function deletePost(postId){
    fetch(`${apiUrl}/${postId}`,
        {method:'DELETE'}
    ).then(response=>{
        if(!response.ok){
            alert("Failes to delete post");
        }
    });
}
