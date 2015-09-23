//选择基准示功图页面，每一张图的urlurl
var cognos_diag_url = "http://9.110.83.168/ibmcognos/cgi-bin/cognos.cgi?b_action=cognosViewer&ui.action=run&ui.object=%2fcontent%2ffolder%5b%40name%3d%27PMQ_WELL%27%5d%2ffolder%5b%40name%3d%27INDICATOR%27%5d%2freport%5b%40name%3d%27Indi_diagram1%27%5d&ui.name=Indi_diagramreport_id&&run.outputFormat=&run.prompt=false&p_pDiagram_ID=diag_id&cv.toolbar=false&cv.header=false";

//示功图叠加页面，iframe的基础url
var src_url = "http://9.110.83.168/ibmcognos/cgi-bin/cognos.cgi?b_action=cognosViewer&ui.action=run&ui.object=%2fcontent%2ffolder%5b%40name%3d%27PMQ_WELL%27%5d%2ffolder%5b%40name%3d%27INDICATOR%27%5d%2freport%5b%40name%3d%27IndicatorAdd%27%5d&ui.name=IndicatorAdd&run.outputFormat=&run.prompt=false&cv.toolbar=false&cv.header=false&p_pJH=%JH%&p_pSortID=0&p_pUserTeam=%userteam%";

//总体概览页面，overview基础url
var report_id_uri = "C:\\Users\\wujz\\Desktop\\CognosReportID.properties";
//var report_id_uri = "/home/MB_OUTPUT/CognosReportID.properties";
var raw_overview_url = "http://9.110.83.168:9080/p2pd/servlet/dispatch/ext/repository/sid/cm/rid/%report_id%/oid/default/content/mht/content";

//结蜡预测页面，基础url
var raw_prediction_url = "http://9.110.83.168/ibmcognos/cgi-bin/cognos.cgi?b_action=cognosViewer&ui.action=run&ui.object=%2fcontent%2ffolder%5b%40name%3d%27PMQ_WELL%27%5d%2ffolder%5b%40name%3d%27PRIDICT%27%5d%2freport%5b%40name%3d%27PredictWell%27%5d&ui.name=PredictWell&run.outputFormat=&run.prompt=false&p_pUserTeam=%userteam%&cv.toolbar=false&cv.header=false";
