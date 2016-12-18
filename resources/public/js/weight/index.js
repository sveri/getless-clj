var ctx=$("#weight-chart");

var data = {
    labels: JSON.parse($("#weighted_at").val()),
    datasets: [
        {
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
            spanGaps: false,
        },

//        {
//            label: "Zucker",
//            fill: false,
//            lineTension: 0.1,
//            backgroundColor: "rgba(75,192,192,0.4)",
//            borderColor: "rgba(75,192,192,1)",
//            borderCapStyle: 'butt',
//            borderDash: [],
//            borderDashOffset: 0.0,
//            borderJoinStyle: 'miter',
//            pointBorderColor: "rgba(75,192,192,1)",
//            pointBackgroundColor: "#fff",
//            pointBorderWidth: 1,
//            pointHoverRadius: 5,
//            pointHoverBackgroundColor: "rgba(75,192,192,1)",
//            pointHoverBorderColor: "rgba(220,220,220,1)",
//            pointHoverBorderWidth: 2,
//            pointRadius: 1,
//            pointHitRadius: 10,
//            data: JSON.parse($("#sugars").val()),
//            spanGaps: false,
//        }
    ]
};

var myLineChart = new Chart(ctx, {
    type: 'line',
    data: data,
});

//var myLineChart = new Chart(ctx, {
//    type: 'line',
//    data: {
//        datasets: [{
//            label: 'Scatter Dataset',
//            data: [[["June","2015"], "July"],
//                   [["June","2015"], "July"]]
//        }]
//    },
//    options: {
//        scales: {
//            xAxes: [{
//                type: 'linear',
//                position: 'bottom'
//            }]
//        }
//    }
//});
