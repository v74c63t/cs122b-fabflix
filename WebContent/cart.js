
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
    if(resultData.length!=0) {
        if (resultData[0]["resultUrl"] != null) {
            let resultTab = jQuery("#result");
            resultTab.attr("href", "result.html?" + resultData[0]["resultUrl"]);
        }
        console.log(resultData);
        let cartTableBody = $("#cart_table_body");
        let totalPrice = 0;
        for (let i = 0; i < resultData.length; i++) {
            let rowHTML = "<tr>";
            rowHTML += "<th>" + resultData[i]['movie_title'] + "</th>";
            rowHTML += "<th><button type='submit' class='btn btn-secondary' style='margin-right:10px'><i class='fa-solid fa-minus'></i></button>" +
                resultData[i]['movie_quantity'] + "<button type='submit' class='btn btn-secondary' style='margin-left:10px'><i class='fa-solid fa-plus'></i></button>" +
                "</th>";
            rowHTML += "<th><button type='submit' class='btn btn-outline-danger'>Delete</button></th>";
            rowHTML += "<th>$" + resultData[i]['movie_price'] + "</th>";
            totalPrice += (parseInt(resultData[i]['movie_quantity']) * parseFloat(resultData[i]['movie_price']));
            rowHTML += "<th>$" + (parseInt(resultData[i]['movie_quantity']) * parseFloat(resultData[i]['movie_price'])).toString() + "</th>";
            rowHTML += '</tr>';
            cartTableBody.append(rowHTML);
        }
        document.getElementById("total").innerText = "Total Price: $" + totalPrice.toString();
    }
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
