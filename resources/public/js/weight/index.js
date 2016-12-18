var ctx=$("#weight-chart");

var data = {
    labels: JSON.parse($("#weighted_at").val()),
    datasets: [
        {
            yAxisID: "y-axis-0",
            label: "Gewichtsverlauf",
            fill: false,
            lineTension: 0.1,
            backgroundColor: "rgba(75,192,192,0.4)",
            borderColor: "rgba(75,192,192,1)",
            borderCapStyle: 'butt',
            borderDash: [],
            borderDashOffset: 0.0,
            borderJoinStyle: 'miter',
            pointBorderColor: "rgba(75,192,192,1)",
            pointBackgroundColor: "#fff",
            pointBorderWidth: 1,
            pointHoverRadius: 5,
            pointHoverBackgroundColor: "rgba(75,192,192,1)",
            pointHoverBorderColor: "rgba(220,220,220,1)",
            pointHoverBorderWidth: 2,
            pointRadius: 1,
            pointHitRadius: 10,
            data: JSON.parse($("#weights").val()),
            spanGaps: true,
        },
        {
            yAxisID: "y-axis-1",
            label: "Zucker",
            fill: false,
            lineTension: 0.1,
            backgroundColor: "rgba(255, 138, 138, 0.4)",
            borderColor: "rgba(255, 138, 138, 1)",
            borderCapStyle: 'butt',
            borderDash: [],
            borderDashOffset: 0.0,
            borderJoinStyle: 'miter',
            pointBorderColor: "rgba(255, 138, 138, 1)",
            pointBackgroundColor: "#fff",
            pointBorderWidth: 1,
            pointHoverRadius: 5,
            pointHoverBackgroundColor: "rgba(75,192,192,1)",
            pointHoverBorderColor: "rgba(220,220,220,1)",
            pointHoverBorderWidth: 2,
            pointRadius: 1,
            pointHitRadius: 10,
            data: JSON.parse($("#sugars").val()),
            spanGaps: true,
        }
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
