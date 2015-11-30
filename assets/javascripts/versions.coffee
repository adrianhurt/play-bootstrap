---
# this ensures Jekyll reads the file to be transformed into JS later
# only Main files contain this front matter, not partials.
---
############################################################################################################
## VERSION FUNCTIONS

baseurl = "{{ site.baseurl }}"

$ ->
	
	defaultVersion = $('body').attr('default-version')
	currentVersion = getVersionFromUrlParams() ? getVersionFromUri() ? defaultVersion
	$('a').click (e) ->
		version = $(this).attr('version') ? currentVersion
		href = hrefWithoutBaseurl $(this).attr('href')
		if href.charAt(0) == '/'
			slug = firstSlug(href)
			if isVersionedUri(firstSlug(href))
				$(this).attr('href', baseurl+'/'+version+href)
			else if version != defaultVersion
				$(this).attr('href', baseurl+href+'?version='+version)
	$badge = $('#current-version .version-badge')
	if $badge.find('.code').text() != currentVersion
		$.getJSON baseurl+'/versions.json', (versions) ->
			version = ($.grep versions, (v) -> v.code == currentVersion)[0]
			$badge.find('.code').text(version.code)
			$badge.find('.play span').html('&nbsp;'+version.play+'&nbsp;')
			$badge.find('.bootstrap span').html('&nbsp;'+version.bootstrap+'&nbsp;')

hrefWithoutBaseurl = (href) -> href.replace(baseurl, "")

getVersionFromUrlParams = () ->
	split = window.location.href.split('?')
	if split.length == 2
		params = split[1].split('&')
		params = (p.split('=') for p in params)
		versionParams = (p for p in params when p[0] == 'version')
		if versionParams.length > 0
			versionParams[0][1]
		else
			null
	else
		null

getVersionFromUri = () ->
	slug = firstSlug(hrefWithoutBaseurl(location.pathname))
	if isVersionedUri(slug) then slug else null

firstSlug = (uri) -> uri.replace(/^\//, "").split('/')[0]

isVersionedUri = (firstSlug) -> firstSlug != "" and firstSlug != "changelog"
