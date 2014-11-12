disableForm = ($form) ->
	$formGroups = $form.find('.form-group:not(.always-editable)')
	$formGroups.find('input:not([type="file"], [type="checkbox"], [type="radio"], [type="hidden"])').removeAttr('disabled').attr('readonly', true)
	$formGroups.find('input[type="file"], input[type="checkbox"], input[type="radio"], select').attr('disabled', true)
	$formGroups.find('.checkbox, .radio, .radio-inline').addClass('disabled')
	$formGroups.find('.checkbox-group, .radio-group, .select-group').find('input[type="hidden"]').removeAttr('disabled readonly')

enableForm = ($form) ->
	$formGroups = $form.find('.form-group:not(.always-editable)')
	$formGroups.find('input:not([type="file"], [type="checkbox"], [type="radio"], [type="hidden"])').removeAttr('disabled readonly')
	$formGroups.find('input[type="file"], input[type="checkbox"], input[type="radio"], select').removeAttr('disabled readonly')
	$formGroups.find('.checkbox, .radio, .radio-inline').removeClass('disabled')
	$formGroups.find('.checkbox-group, .radio-group, .select-group').find('input[type="hidden"]').removeAttr('readonly').attr('disabled', true)

############################################################################################################
## DOCUMENT IS READY - INIT APP
############################################################################################################
$ ->
	
	$('.btn-readonly-unlock').click (e) ->
		if $(this).hasClass('locked')
			$(this).removeClass('locked btn-primary').addClass('btn-danger').text('Lock readonly fields')
			enableForm($('#form-readonly'))
		else
			$(this).removeClass('btn-danger').addClass('locked btn-primary').text('Unlock readonly fields')
			disableForm($('#form-readonly'))
	
	# Change also the value of its companion input
	$('.checkbox-group input[type="checkbox"]').change ->
		$(this).parents('.checkbox-group').find('input[type="hidden"]').val $(this).prop('checked')
	$('.radio-group input[type="radio"]').change ->
		$radioGroup = $(this).parents('.radio-group')
		$radioGroup.find('input[type="hidden"]').val $radioGroup.find('input[type="radio"]:checked').val()
	$('.select-group select').change ->
		$(this).parents('.select-group').find('input[type="hidden"]').val $(this).val()
	
	$('.input-daterange').datepicker
		format: "dd-mm-yyyy"
		todayBtn: "linked"
		todayHighlight: true

	$('body[tab="docs"]').scrollspy
		target: '#sidebar'
		offset: 60

	$('#sidebar a[href*=#]:not([href=#])').click (e) ->
		if (location.pathname.replace(/^\//,'') == this.pathname.replace(/^\//,'') && location.hostname == this.hostname)
			target = $(this.hash)
			target = if target.length then target else $('[name=' + this.hash.slice(1) +']')
			if (target.length)
				$('html,body').animate({
					scrollTop: target.offset().top - 50
				}, 500)
				false

	
	$('.apply-tweak').click (e) ->
		if $(this).hasClass('active')
			$('.form-inline').removeClass('align-top')
			$(this).removeClass('btn-danger').addClass('btn-info')
		else
			$('.form-inline').addClass('align-top')
			$(this).removeClass('btn-info').addClass('btn-danger')