var ctx=$("#weight-chart");

function get_data_set(label, hidden, yAxis, color, backgroundColor, jsonData) {
    return {
                hidden: hidden,
                yAxisID: yAxis,
                label: label,
                fill: false,
                lineTension: 0.1,
                backgroundColor: backgroundColor,
                borderColor: color,
                borderCapStyle: 'butt',
                borderDash: [],
                borderDashOffset: 0.0,
                borderJoinStyle: 'miter',
                pointBorderColor: color,
                pointBackgroundColor: "#fff",
                pointBorderWidth: 1,
                pointHoverRadius: 5,
                pointHoverBackgroundColor: color,
                pointHoverBorderColor: "rgba(220,220,220,1)",
                pointHoverBorderWidth: 2,
                pointRadius: 1,
                pointHitRadius: 10,
                data: jsonData,
                spanGaps: true,
            }
};

var data = {
    labels: JSON.parse($("#weighted_at").val()),
    datasets: [get_data_set($("#weight_string").val(), false, "y-axis-0", "rgba(75,192,192,1)", "rgba(75,192,192,0.4)", JSON.parse($("#weights").val())),
        get_data_set($("#sugar_string").val(), false, "y-axis-1", "rgba(255, 138, 138, 1)", "rgba(255, 138, 138, 0.4)", JSON.parse($("#sugars").val())),
        get_data_set($("#fat_string").val(), true, "y-axis-1", "rgba(110, 255, 98, 1)", "rgba(110, 255, 98, 0.4)", JSON.parse($("#fats").val())),
        get_data_set($("#energy_string").val(), true, "y-axis-1", "rgba(0, 116, 7, 1)", "rgba(0, 116, 7, 0.4)", JSON.parse($("#energy").val()))
    ]
};

var myLineChart = new Chart(ctx, {
    type: 'line',
    data: data,
    options: {
      scales: {
           yAxes: [{
             position: "left",
             "id": "y-axis-0"
           }, {
             position: "right",
             "id": "y-axis-1"
           }]
         }
     },
});
