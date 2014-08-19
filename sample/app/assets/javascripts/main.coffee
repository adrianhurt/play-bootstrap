############################################################################################################
## DOCUMENT IS READY - INIT APP
############################################################################################################
$ ->
	
	$('.apply-tweak').click (e) ->
		if $(this).hasClass('active')
			$('.form-inline').removeClass('align-top')
			$(this).removeClass('btn-danger').addClass('btn-info')
		else
			$('.form-inline').addClass('align-top')
			$(this).removeClass('btn-info').addClass('btn-danger')