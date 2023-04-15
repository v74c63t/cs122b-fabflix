// TO DO
function handleGenreResult(resultData) {
    console.log("handleResult: populating movies info from resultData");

    let genreList = jQuery("#genre-list");

    for (let i = 0; i < resultData.length; i++) {
        let genre = resultData[i]["genre_name"];
        let listHTML = "<h4>" + genre + "</h4>";
        // add in the href later
        genreList.append(listHTML);
    }
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/genres", // Setting request url, which is mapped by MoviesServlet
    success: (resultData) => handleGenreResult(resultData) // Setting callback function to handle data returned successfully by the MoviesServlet
});