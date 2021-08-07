const toggleSidebar = () =>{
    if($(".sidebar").is(":visible")){


        $(".sidebar").css("display","none");
        $(".content").css("margin-left","0%");
    }else{
        $(".sidebar").css("display","block");
        $(".content").css("margin-left","20%");
    }
}

function deleteContact(cid){
	swal({
		title: "Are you sure?",
		text: "Once deleted, you will not be able to recover this contact!",
		icon: "warning",
		buttons: true,
		dangerMode: true,
	  })
	  .then((willDelete) => {
		if (willDelete) {
			window.location="/user/"+cid+"/delete/";
		  
		} else {
		  swal("Hush! That was a close one!!");
		}
	  });
}
