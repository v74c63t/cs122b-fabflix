function handleMetadata(resultData) {
    console.log("PLACEHOLDER")
    console.log(resultData)
}

$.ajax(
    "../api/metadata", {
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        success: (resultData) => handleMetadata(resultData) // Setting callback function to handle data returned successfully by the MainInitServlet
    }
);