// Create the chart
	Highcharts.mapChart('container-4', {
		credits:{
			enabled: false
		},
		chart: {
			map: 'countries/fr/custom/fr-all-mainland'
		},
		title: {
			text: '法国肺炎分布'
		},
		subtitle: {
			text: '来源：综合数据'
		},
		exporting: {
			  enabled: false
		},
		mapNavigation: {
			enabled: true,
			buttonOptions: {
				verticalAlign: 'bottom'
			}
		},
		colorAxis: {
			min: 0
		},
		series: [{
			data: mapData,
			name: '确诊数',
			states: {
				hover: {
					color: '#BADA55'
				}
			},
			dataLabels: {
				enabled: true,
				format: '{point.name}'
			}
		}]
	});