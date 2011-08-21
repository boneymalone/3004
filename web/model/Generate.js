$("#su-submit").click(function() {
	window.location = "api/report.jsp?type=system"
});

$("#sh-submit").click(function() {
	window.location = "api/report.jsp?type=session"
});

dbPoll.api("api/report.jsp", {userID: 2, type: "pdata"}, function(data) {
	var i = 0, l = data.length, html = "", p;
	
	for(; i < l; ++i) {
		p = data[i];
		html += "<option value='"+p.pollID+"'>"+p.PollName+"</option>";
	}
	
	$("#polls").html(html).trigger("change");
        
});

$("#polls").change(function() {

    var id = $(this).val();
    dbPoll.api("api/report.jsp", {pollID: id, type: "qdata"}, function(data) {

    var i = 0, l = data.length, html = "", p;

    html += "Individual";

    for(; i < l; ++i) {

        p = data[i];
        html += ""+p.Question+"";

    }



    



    $("#demog").html(html).trigger("change");

    });

});


$("#demog").change(function() {
	var id = $("#polls").val();
	
	if($(this).val() === "individual") {
		dbPoll.api("api/report.jsp", {pollID: id, demotype: "individual", type: "idata"}, function(data) {
			var i = 0, l = data.length, html = "<option value=''>---</option>", user;
		
			for(; i < l; ++i) {
				user = data[i];
				
				html += "<option value='"+user.id+"'>"+user.UserName+"</option>";
			}
			
			$("#value").html(html);
		});
		
		$("#chartsel").html("<option value='Bar'>Bar</option>");
		$("#location").attr('disabled', true);
	} else {
                dbPoll.api("api/report.jsp", {pollID: id, questID: $(this).val(), type: "adata"}, function(data) {
			var i = 0, l = data.length, html = "<option value=''>---</option>", user;
		
			for(; i < l; ++i) {
				user = data[i];
				
				html += "<option value='"+user.id+"'>"+user.UserName+"</option>";
			}
			
			$("#value").html(html);
		});
            
		$("#chartsel").html("<option value='Bar'>Bar</option><option value='Pie'>Pie</option>");
		$("#location").removeAttr("disabled");
	}
});


$("#sr-submit").click(function() {
	var param = {};
	param.id = $("#polls").val(),
	param.demoID = $("#demog").val();
	param.demoValue = $("#value").val();
	param.graph = $("#chartsel").val();
	param.location = $("#location").val();
	
	window.location = "api/report.jsp?"+$.param(param);
});