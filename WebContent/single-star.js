function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
// note: have to modify and adjust to use code
function handleResult(resultData) {

    console.log("handleResult: populating star info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let starName = jQuery("#star_name");
    starName.append(resultData[0]["star_name"])
    let starInfoElement = jQuery("#star_info");

    if(resultData[0]["star_dob"] != null){
        starInfoElement.append("<div><i><strong>" + resultData[0]["star_name"] + "</strong></i><span style='font-size: 24px'> (" +
            + resultData[0]["star_dob"] + ")</span></div>");
    }
    else{
        starInfoElement.append("<div><i><strong>" + resultData[0]["star_name"] + "</strong></i><span style='font-size: 24px'> (" +
            "N/A)</span></div>");
    }

    console.log("handleResult: populating movie table from resultData");
    console.log(resultData);

    // Populate table
    let starMovieTableBodyElement = jQuery("#star_movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + "<a href='single-movie.html?id=" + resultData[i]["movie_id"]
            + "'>" + resultData[i]["movie_title"] + "</a></th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "</tr>";
        console.log(rowHTML);
        // Append the row created to the table body, which will refresh the page
        starMovieTableBodyElement.append(rowHTML);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let starId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-star?id=" + starId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});

