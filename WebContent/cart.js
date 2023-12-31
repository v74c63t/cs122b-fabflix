
let movie_search_form = $("#movie-search-form");


function handleSearch(searchSubmitEvent) {
    let paramArray = []
    $(".search-item").each( function(i, e) {
        if ($(this)[0].value != "") {
            paramArray.push([$(this)[0].name, $(this)[0].value]);
        }
    })

    searchSubmitEvent.preventDefault();

    let url = "";
    for (let i = 0; i < paramArray.length; i++) {
        if (i == paramArray.length-1) {
            url += paramArray[i][0] + "=" + paramArray[i][1];
            url += "&sortBy=title+ASC+rating+ASC&numRecords=25&firstRecord=0";
        } else {
            url += paramArray[i][0] + "=" + paramArray[i][1] + "&";
        }
    }
    window.location.replace("result.html?" + url);
}

/**
 * Handle the items in item list
 * @param resultArray jsonObject, needs to be parsed to html
 */
function handleCartArray(resultData) {
    let cartTableBody = $("#cart_table_body");
    cartTableBody.html("");
    document.getElementById("total").innerHTML="";
    if(resultData.length!=0) {
        if (resultData.hasOwnProperty("resultUrl")){
            let resultTab = jQuery("#result");
            resultTab.attr("href", "result.html?" + resultData["resultUrl"]);
        }
        else {
            if (resultData[0]["resultUrl"] != null) {
                let resultTab = jQuery("#result");
                resultTab.attr("href", "result.html?" + resultData[0]["resultUrl"]);
            }
            // console.log(resultData);
            let totalPrice = 0;
            for (let i = 0; i < resultData.length; i++) {
                let rowHTML = "<tr>";
                rowHTML += "<th>" +
                    '<a style="color:darkturquoise;" href="single-movie.html?id=' + resultData[i]["movie_id"] + '">' +
                    resultData[i]["movie_title"] +
                    '</a>'+
                    "</th>";
                rowHTML += "<th><button type='submit' class='btn btn-secondary' style='margin-right:10px' onclick=\"handleCart("+ "'" + resultData[i]["movie_id"] + "'" + ", -1.0)\"><i class='fa-solid fa-minus'></i></button>" +
                    resultData[i]['movie_quantity'] + "<button type='submit' class='btn btn-secondary' style='margin-left:10px' onclick=\"handleCart("+ "'" + resultData[i]["movie_id"] + "'" + ", 1.0)\"><i class='fa-solid fa-plus'></i></button>" +
                    "</th>";
                rowHTML += "<th><button type='submit' class='btn btn-outline-danger' onclick=\"handleCart("+ "'" + resultData[i]["movie_id"] + "'" + ", 0.0)\">Delete</button></th>";
                rowHTML += "<th>$" + resultData[i]['movie_price'].toFixed(2) + "</th>";
                totalPrice += parseFloat((parseInt(resultData[i]['movie_quantity']) * parseFloat(resultData[i]['movie_price'])).toFixed(2));
                rowHTML += "<th>$" + (parseInt(resultData[i]['movie_quantity']) * parseFloat(resultData[i]['movie_price'])).toFixed(2).toString() + "</th>";
                rowHTML += '</tr>';
                cartTableBody.append(rowHTML);
            }
            document.getElementById("total").innerText = "Total Price: $" + totalPrice.toFixed(2);
        }
    }
}

function handleCart(movieId, quantity) {
    $.ajax("api/cart", {
        method: "POST",
        data: {item: movieId, quantity: quantity},
        success: resultDataString => {
            // dynamically update the page to reflect changes
            $.ajax("api/cart", {
                method: "GET",
                success: handleCartArray
            });
        }
    })

}

/**
 * Submit form content with POST method
 * @param cartEvent
 */


$.ajax("api/cart", {
    method: "GET",
    success: handleCartArray
});


movie_search_form.submit(handleSearch);
