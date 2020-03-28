Highcharts.chart('container-1', {
    credits:{
        enabled: false
    },
    title: {
        text: '法国肺炎疫情数据'
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
    series: totalRecapData,
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