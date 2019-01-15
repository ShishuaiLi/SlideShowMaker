var slides=json.slides;
var slash="/";
var slideNum=0;
var slidesSize=slides.length;

var title=document.getElementById("title");
var stage=document.getElementById("stage");
var image=stage.children[0];
var caption=document.getElementById("caption");
var buttons=document.getElementById("buttons").children;
var previous=document.getElementById("previous");
var play=document.getElementById("play");
var pause=document.getElementById("pause");
var next=document.getElementById("next");

document.title=json.title;
title.innerHTML=json.title;
for (var i=0; i < buttons.length; i++) {
	buttons[i].onmouseover=function () {
		(function(obj){
		obj.style.outline="4px solid #99FF33";}) (this);
	};
	buttons[i].onmouseout=function () {
		(function(obj){
		obj.style.outline="";}) (this);
	};
}
updateSlide();

previous.setAttribute("src","previous.png");
previous.setAttribute("alt","previous.png");
previous.onclick=goToPre;
next.setAttribute("src","next.png");
next.setAttribute("alt","next.png");
next.onclick=goToNext;
play.setAttribute("src","play.png");
play.setAttribute("alt","play.png");
play.onclick=begin;
pause.setAttribute("src","pause.png");
pause.setAttribute("alt","pause.png");
pause.style.display="none";
pause.onclick=stop;

function goToPre() {
	if(slideNum >0) slideNum--;
    updateSlide();
}

function goToNext() {
	if(slideNum < slidesSize-1) {
		slideNum++;
		updateSlide();
	}
	else if(play.style.display=="none") {
		window.clearInterval(timer);
		stop();		
	}    
}

function begin() {
	play.style.display="none";
	pause.style.display="inline";
	timer=window.setInterval(goToNext,2000);
}

function stop() {
	window.clearInterval(timer);
	pause.style.display="none";
	play.style.display="inline";
}

function updateSlide() {
	image.setAttribute("src",slides[slideNum].image_path+slash+slides[slideNum].image_file_name);
    image.setAttribute("alt",slides[slideNum].image_file_name);
    caption.innerHTML=slides[slideNum].caption;
}