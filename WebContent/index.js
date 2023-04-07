
function handleMovieResult(resultData) {
    let moviesTableBodyElem = jQuery("#movies_table_body");
    console.log(resultData)

    // add rest
    // for (let i = 0; i < Math.min(20, resultData.length); i++) {
    //     let rowHTML = "";
    //     rowHTML +="<tr>"
    //     rowHTML +=
    //         "<th>" +
    //         "<a href='single-movie.html?id' + resultData[i]["
    // }
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movies", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});
