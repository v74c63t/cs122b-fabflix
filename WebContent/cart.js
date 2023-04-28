
let movie_search_form = $("#movie-search-form");
// let cart = $("#cart");

// /**
//  * Handle the data returned by IndexServlet
//  * @param resultDataString jsonObject, consists of session info
//  */
// function handleSessionData(resultDataString) {
//     let resultDataJson = JSON.parse(resultDataString);
//
//     if(resultDataJson["resultUrl"] != null) {
//         let resultTab = jQuery("#result");
//         resultTab.attr("href", "result.html?" + resultDataJson["resultUrl"]);
//     }
//
//     console.log("handle session response");
//     console.log(resultDataJson);
//     console.log(resultDataJson["sessionID"]);
//
//     // show the session information
//     $("#sessionID").text("Session ID: " + resultDataJson["sessionID"]);
//     $("#lastAccessTime").text("Last access time: " + resultDataJson["lastAccessTime"]);
//
//     // show cart information
//     handleCartArray(resultDataJson["previousItems"]);
// }

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
    if(resultData[0]["resultUrl"] != null) {
        let resultTab = jQuery("#result");
        resultTab.attr("href", "result.html?" + resultData[0]["resultUrl"]);
    }
    console.log(resultData);
    let cartTableBody = $("#cart_table_body");
    // change it to html list
    // let rowHTML = "<ul>";
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
    // res += "</ul>";

    // clear the old array and show the new array in the frontend
    // item_list.html("");
    // item_list.append(res);
}

/**
 * Submit form content with POST method
 * @param cartEvent
 */
// function handleCartInfo(cartEvent) {
//     console.log("submit cart form");
//     /**
//      * When users click the submit button, the browser will not direct
//      * users to the url defined in HTML form. Instead, it will call this
//      * event handler when the event is triggered.
//      */
//     cartEvent.preventDefault();
//
//     $.ajax("api/cart", {
//         method: "POST",
//         data: cart.serialize(),
//         success: resultDataString => {
//             let resultDataJson = JSON.parse(resultDataString);
//             handleCartArray(resultDataJson["previousItems"]);
//         }
//     });
//
//     // clear input form
//     cart[0].reset();
// }

$.ajax("api/cart", {
    method: "GET",
    success: handleCartArray
});

// Bind the submit action of the form to a event handler function
// cart.submit(handleCartInfo);

movie_search_form.submit(handleSearch);
