Highcharts.chart('container-2', {
    credits:{
        enabled: false
    },
    title: {
        text: '新冠肺炎法国每日变化图'
    },
    subtitle: {
        text: '来源： 综合数据'
    },
    exporting: {
      enabled: false
    },
    yAxis: {
        title: {
            text: '例'
        }
    },
    xAxis: {
        type: 'datetime',
        dateTimeLabelFormats: {
          millisecond: myDateFormat,
          second: myDateFormat,
          minute: myDateFormat,
          hour: myDateFormat,
          day: myDateFormat,
          week: myDateFormat,
          month: myDateFormat,
          year: myDateFormat
      }
    },
    legend: {
        layout: 'vertical',
        align: 'right',
        verticalAlign: 'middle'
    },
    series: diffRecapData,
    responsive: {
        rules: [{
            condition: {
                maxWidth: 500
            },
            chartOptions: {
                legend: {
                    layout: 'horizontal',
                    align: 'center',
                    verticalAlign: 'bottom'
                }
            }
        }]
    }

});