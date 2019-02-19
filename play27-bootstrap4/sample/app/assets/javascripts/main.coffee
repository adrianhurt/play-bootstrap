
############################################################################################################
## Smoth scroll for docs

scrollWithAnimation = ($id, duration) ->
	$('html,body').animate({scrollTop: $id.offset().top - 55}, duration)

############################################################################################################
## For readonly example

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
		
	## For readonly example
	$('.btn-readonly-unlock').click (e) ->
		if $(this).hasClass('locked')
			$(this).removeClass('locked btn-primary').addClass('btn-danger').text('Lock readonly fields')
			enableForm($('#form-readonly'))
		else
			$(this).removeClass('btn-danger').addClass('locked btn-primary').text('Unlock readonly fields')
			disableForm($('#form-readonly'))
	if /\/readonly\/?\?\w/.test(window.location.href)
		params = window.location.href.split('?')[1].split('&')
		params = (p.split('=') for p in params)
		getParam = (name, params) ->
			foundParams = (p for p in params when p[0] == name)
			if foundParams.length > 0 then foundParams[0][1] else undefined
		text = getParam "text", params
		checkbox = getParam "checkbox", params
		radio = getParam "radio", params
		select = getParam "select", params
		if text? or checkbox? or radio? or select?
			$data = $('#bound-data')
			$data.find('#data-text').text text
			$data.find('#data-checkbox').text checkbox
			$data.find('#data-radio').text radio
			$data.find('#data-select').text select
			$data.removeAttr('hidden')

			
				
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

	$('a[href*="#"]:not([href="#"], [href*="#collapse"], [data-toggle])').click (e) ->
		if (location.pathname.replace(/^\//,'') == this.pathname.replace(/^\//,'') && location.hostname == this.hostname)
			target = $(this.hash)
			target = if target.length then target else $('[name=' + this.hash.slice(1) +']')
			if (target.length)
				scrollWithAnimation(target, 500)
	
	hash = location.hash
	if hash.length > 0
		scrollWithAnimation($(hash), 10)

	
	$('.apply-tweak').click (e) ->
		if $(this).hasClass('active')
			$('.form-inline').removeClass('align-top')
			$(this).removeClass('btn-danger').addClass('btn-info')
		else
			$('.form-inline').addClass('align-top')
			$(this).removeClass('btn-info').addClass('btn-danger')
	
	
	
	$('.input-number-plus').click (e) ->
		$input = $(this).parent().find('input')
		current = parseInt $input.val(), 10
		$input.val current+1
		
	$('.input-number-minus').click (e) ->
		$input = $(this).parent().find('input')
		current = parseInt $input.val(), 10
		$input.val current-1