function handleMetadata(resultData) {
    console.log(resultData)
    for (let i = 0; i < resultData.length; i++) {
        let table_name = resultData[i]["table_name"];
        let fieldsArray = resultData[i]['fields'];
        let typesArray = resultData[i]['types'];

        let metadata = $("#metadata-table-body");

        let table_html = "";

        table_html += "<h1 class='page-headers'><strong> " + table_name + "</strong></h1>";
        table_html += "<div style='margin-left: 20%; margin-right: 20%;' class='rounded'>";
        table_html += "<div style='padding-left: 80px; padding-right: 80px;'>";
        table_html += "<table id='dashboard-table' class='table table-striped table-dark table-borderless rounded'>";
        table_html += "<thead class='text-warning'>";
        table_html += "<tr> <th style='width: 50%;text-align: center'>Attribute</th> <th style='width: 50%;text-align: center'>Type</th> </tr>";
        table_html += "</thead>";
        table_html += "<tbody id='" + table_name + "-table-body'></tbody>";
        table_html += "</table></div></div>";

        metadata.append(table_html)

        let table_id = "#"+ table_name + "-table-body";
        let dashboard_table = $(table_id);

        for (let i = 0; i < fieldsArray.length; i++) {
            let rowHTML = "";
            rowHTML += "<tr>"
            rowHTML += "<th style='width: 50%;text-align: center'>" + fieldsArray[i] + "</th>";
            rowHTML += "<th style='width: 50%;text-align: center'>" + typesArray[i] + "</th>";
            rowHTML += "</tr>";

            dashboard_table.append(rowHTML);
        }
    }
}

$.ajax(
    "../api/metadata", {
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        success: (resultData) => handleMetadata(resultData) // Setting callback function to handle data returned successfully by the MainInitServlet
    }
);