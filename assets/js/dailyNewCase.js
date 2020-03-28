Highcharts.chart('container-2', {
			credits:{
				enabled: false
			},
			chart: {
				type: 'column'
			},
			exporting: {
			  enabled: false
		    },
			title: {
				text: '法国肺炎每日新增数'
			},
			subtitle: {
				text: '来源：综合数据'
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
			yAxis: {
				min: 0,
				title: {
					text: '例'
				}
			},
			legend: {
				enabled: false
			},
			tooltip: {
				pointFormat: '新增确诊 <b>{point.y} 例</b>'
			},
			series: [{
                        name: 'New Cases',
                        data: dailyNewCaseData,
                        dataLabels: {
                            enabled: true,
                            rotation: -90,
                            color: '#FFFFFF',
                            align: 'right',
                            format: '{point.y}', // one decimal
                            y: 10, // 10 pixels down from the top
                            style: {
                                fontSize: '13px',
                                fontFamily: 'Verdana, sans-serif'
                            }
                        }
                    }]
		});