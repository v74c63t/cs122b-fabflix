// let payment_form = $("payment-form");

// function handlePaymentResult(resultDataString) {
//     let resultDataJson = JSON.parse(JSON.stringify(resultDataString));
//
//     console.log("handle login response");
//     console.log(resultDataJson);
//     console.log(resultDataJson["status"]);
//
//     // If login succeeds, it will redirect the user to index.html
//     if (resultDataJson["status"] === "success") {
//         // window.location.replace("main.html");
//     } else {
//         // If login fails, the web page will display
//         // error messages on <div> with id "login_error_message"
//         console.log("show error message");
//         console.log(resultDataJson["message"]);
//         $("#login_error_message").text(resultDataJson["message"]);
//     }
// }


// function submitPaymentForm(formSubmitEvent) {
//     console.log("submit payment form");
//     /**
//      * When users click the submit button, the browser will not direct
//      * users to the url defined in HTML form. Instead, it will call this
//      * event handler when the event is triggered.
//      */
//     formSubmitEvent.preventDefault();
//
//     $.ajax(
//         "api/payment", {
//             method: "POST",
//             // Serialize the login form to the data sent by POST request
//             data: login_form.serialize(),
//             success: handlePaymentResult
//         }
//     );
// }

// payment_form.submit(submitPaymentForm());
