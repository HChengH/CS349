#include <iostream>
#include <list>
#include <cstdlib>
#include <vector>
#include <unistd.h>

#include <X11/Xlib.h>
#include <X11/Xutil.h>

#include <sstream>  
#include <string>

#include <sys/time.h>

using namespace std;

enum Direction{upDir, downDir, leftDir, rightDir};

const int Border = 5;
const int BufferSize = 10;
int fps = 30;

Pixmap buffer;

struct XInfo {
    Display*  display;
    Window   window;
    GC       gc;
};

class Displayable {
public:
    virtual void paint(XInfo &xinfo) = 0;
    virtual void reset(XInfo &xinfo) = 0;
};

class Frog : public Displayable{
public:
	virtual void paint(XInfo &xinfo){
		int screenNum = DefaultScreen(xinfo.display);
		XSetForeground(xinfo.display, xinfo.gc, BlackPixel(xinfo.display, 
			 										 screenNum));
		XFillRectangle(xinfo.display, xinfo.window, xinfo.gc, 
			           this->x, this->y, 50, 50);
	}

	Frog(int xCord, int yCord):x(xCord), y(yCord){
		this->width = 50;
		this->height = 50;
	}

	virtual void reset(XInfo &xinfo){
		XWindowAttributes wa;
		XGetWindowAttributes(xinfo.display, xinfo.window, &wa);
		this->x = (wa.width/2) -(this->width/2);
		this->y = wa.height - this->height;
	}

	void move(Direction dir, XInfo &xinfo){
		XWindowAttributes wa;
		XGetWindowAttributes(xinfo.display, xinfo.window, &wa);
		int width = this->width;
		int height = this->height;
		switch(dir){
			case upDir:
				if(this->y - height >= 0){
					this->y -= height;
				}
				break;

			case downDir:
				if(this->y + height <=  wa.height - height && 
				   this->y >= 50){
					this->y += height;
				}
				break;

			case leftDir:
				if(this->x - width >= 0){
					this->x -= width;
				}
				break;

			// right
			default:
				if(this->x + width <= wa.width -width){
					this->x += width;
				}
				break;
		}
	}

	bool inPosition(){
		return this->y == 0;
	}

	int getyCor(){
		return this->y;
	}

	int getXcord(){
		return this->x;
	}

	int getWidth(){
		return this->width;
	}

	int getHeight(){
		return this->height;
	}

private:
	int x;
	int y;
	int width;
	int height;
};

class Obstacle : public Displayable{
public:
	virtual void paint(XInfo &xinfo){
		int screenNum = DefaultScreen(xinfo.display);
		XSetForeground(xinfo.display, xinfo.gc, BlackPixel(xinfo.display, 
			 										 screenNum));
		XFillRectangle(xinfo.display, xinfo.window, xinfo.gc, 
			           this->x, this->y, this->width, this->height);
	}

	virtual void reset(XInfo &xinfo){
		this->speed = 1;
	}

	Obstacle(int xCord, int yCord, int Owidth, int Oheight, int Orow, int Ointerval, int OwindowWidth): x(xCord), 
	   y(yCord), width(Owidth), height(Oheight), row(Orow), interval(Ointerval), windowWidth(OwindowWidth){
		this->speed = 1;
	}

	void speedUp(){
		++this->speed;
	}

	void move(XInfo &xinfo){
		if(this->row == 2){
			this->x -= speed;
			if(this->x < -this->width){
				this->x = this->windowWidth + interval;
			}
		}
		else{
			this->x += speed;
			if(this->x > this->windowWidth){
				this->x = -interval;
			}
		}
	}

	int getRow(){
		return this->row;
	}

	int getWidth(){
		return this->width;
	}

	int getXcord(){
		return this->x;
	}
private:
	int x;
	int y;
	int width;
	int height;
	int speed;
	int row;
	int interval;
	int windowWidth;
};

class Level: public Displayable{
public:
	virtual void paint(XInfo &xinfo){
		string level_str;
		ostringstream stream;
		stream << "Level: ";
		stream << this->level;
		level_str = stream.str();


		XDrawImageString( xinfo.display, xinfo.window, xinfo.gc,
                          this->x, this->y, level_str.c_str(), level_str.length() );

	}
	Level(int xCord, int yCord):x(xCord), y(yCord){
		this->level = 1;
	}

	void nextLevel(){
		++this->level;
	}

	virtual void reset(XInfo &xinfo){
		this->level = 1;
	}

private:
	int level;
	int x;
	int y;
};


void error(string str) {
    cerr << str << endl;
    exit(0);
}

unsigned long now() {
	timeval tv;
	gettimeofday(&tv, NULL);
	return tv.tv_sec * 1000000 + tv.tv_usec;
}

bool collision(Frog *frog, Obstacle *obs){
	int range_base = obs->getXcord();
	int range_max = range_base + obs->getWidth();

	int frog_pos_x = frog->getXcord();
	if((frog_pos_x + frog->getWidth() >= range_base && frog_pos_x <= range_max) || 
	   (frog_pos_x >= range_base && frog_pos_x <= range_max)){
	   	return true;

	}
	return false;
}

bool repaint(list<Displayable*> dList, XInfo& xinfo) {
    list<Displayable*>::const_iterator begin = dList.begin();
    list<Displayable*>::const_iterator end = dList.end();

    int counter = 1;
    //Pixmap pixmap;
    //pixmap = buffer;

    //XSetForeground(display, gc, WhitePixel(display, DefaultScreen(display)));
	//XFillRectangle(display, pixmap, gc, 0, 0, w.width, w.height);
    XClearWindow( xinfo.display, xinfo.window );
    Frog *frog = ((Frog *)*begin);
    int section = frog->getyCor() / frog->getHeight();

    while ( begin != end ) {
        Displayable* d = *begin;
        if(counter >2){
        	Obstacle *o = ((Obstacle *)d);
        	o->move(xinfo);
        	// performe a collision test...
        	if(o->getRow() == section && collision(frog, o)){
        		return false;
        	}
        }
        d->paint(xinfo);
        begin++;
        ++counter;
    }
    XFlush( xinfo.display );
    return true;
}

void resetAll(list<Displayable*> dList, XInfo& xinfo){
    list<Displayable*>::const_iterator begin = dList.begin();
    list<Displayable*>::const_iterator end = dList.end();
    while(begin != end){
    	Displayable *d = *begin;
    	d->reset(xinfo);
    	++begin;
    }
}

// The loop responding to events from the user.
void eventloop(XInfo& xinfo, list<Displayable*> dList) {
    XEvent event;
    KeySym key;
    char text[BufferSize];
    list<Displayable*>::const_iterator begin = dList.begin();
    Frog *frog = (Frog *)*begin;
    unsigned long lastRepaint = 0;

    while (true) {
    	if(XPending(xinfo.display) > 0){
    		XNextEvent(xinfo.display, &event);
        	switch (event.type) {
	        	case KeyPress:
	            	int i = XLookupString( 
	                	(XKeyEvent*)&event, text, BufferSize, &key, 0);

	            	if (i == 1 && text[0] == 'q') {
	                	cout << "Terminated normally." << endl;
	                	XCloseDisplay(xinfo.display);
	                	return;
	            	}

	            	switch(key){
	                	case XK_Up:
	                    	frog->move(upDir, xinfo);
	                    	break;
	                	case XK_Down:
	                    	frog->move(downDir, xinfo);
	                    	break;
	                	case XK_Left:
	                    	frog->move(leftDir, xinfo);
	                    	break;
	                	case XK_Right:
	                    	frog->move(rightDir, xinfo);
	                    	break;
	                	case XK_n:
	                		if(frog->inPosition()){
	                			int counter = 1;
	            				begin = dList.begin();
	            				list<Displayable*>::const_iterator end = dList.end();
	            				while(begin != end){
	            					Displayable *d = *begin;
	            					if(counter == 2){
	            						((Level *)d)->nextLevel();
	            					}
	            					else{
	            						if(counter >2){
	            							((Obstacle *)d)->speedUp();
	            						}
	            						else{
	            							d->reset(xinfo);
	            						}
	            					}
	            					++counter;
	            					begin++;
	            				}
	            				repaint(dList, xinfo);
	                		}
	                		break;
	                	}
	            	break;
        	}
    	}
        
        unsigned long end = now();
        if(end - lastRepaint > 1000000 /fps){
        	bool status = repaint(dList, xinfo);
        	if(!status){
        		resetAll(dList, xinfo);
        		repaint(dList, xinfo);
        	}
        	lastRepaint = now();
        }

        if (XPending(xinfo.display) == 0) {
			usleep(1000000 / fps - (end - lastRepaint));
		}
    }
}

list<Displayable *> createObstacal(XInfo &xinfo){
	int obs50Px = 4;
	int obs20Px = 5;
	int obs100Px = 3;

	int row = 1;
	list<Displayable *> dList;
	XWindowAttributes wa;
	XGetWindowAttributes(xinfo.display, xinfo.window, &wa);
	int width = wa.width;

	int interval = width / (obs50Px-1);
	for(int i = 0; i < obs50Px; ++i){
		dList.push_back(new Obstacle((i-1)*interval, 50*row, 50, 50, row, interval, width));
	}
	interval = width / (obs20Px-1);
	++row;
	for(int i = 0; i < obs20Px; ++i){
		dList.push_back(new Obstacle((i)*interval, 50*row, 20, 50, row, interval, width));
	}
	interval = width / (obs100Px-1);
	++row;
	for(int i = 0; i < obs100Px; ++i){
		dList.push_back(new Obstacle((i-1)*interval, 50*row, 100, 50, row, interval, width));
	}
	return dList;
}

//  Create the window;  initialize X.
 list<Displayable*> initX(int argc, char* argv[], XInfo &xinfo) {

 	list<Displayable*> dList;

    xinfo.display = XOpenDisplay("");
    if (!xinfo.display) {
        error("Can't open display.");
    }

    int screen = DefaultScreen( xinfo.display );
    unsigned long background = WhitePixel(xinfo.display, screen);
    unsigned long foreground = BlackPixel(xinfo.display, screen);

 
    XSizeHints hints;
    hints.x = 100;
    hints.y = 100;
    hints.width = 850;
    hints.height = 250;
    hints.flags = PPosition | PSize;
    xinfo.window = XCreateSimpleWindow( xinfo.display, DefaultRootWindow( xinfo.display),
                                        hints.x, hints.y, hints.width, hints.height,
                                        Border, foreground, background);
    XSetStandardProperties( xinfo.display, xinfo.window, "Frog", "Frog", None,
                            argv, argc, &hints);


    xinfo.gc = XCreateGC (xinfo.display, xinfo.window, 0, 0);
    XSetBackground( xinfo.display, xinfo.gc, background);
    XSetForeground( xinfo.display, xinfo.gc, foreground);
    XSetFillStyle(xinfo.display,  xinfo.gc, FillSolid);

    XFontStruct * font;
  	font = XLoadQueryFont (xinfo.display, "12x24");
  	XSetFont (xinfo.display, xinfo.gc, font->fid);

    // draw frog
    Frog *newfrog = new Frog(hints.width/2 - 25, hints.height - 50);
    Level *newLevel = new Level(700,30);

    dList.push_back(newfrog);
    dList.push_back(newLevel);

    list<Displayable *>temp = createObstacal(xinfo);
    dList.splice(dList.end(), temp);

    //int depth = DefaultDepth(xinfo.display, DefaultScreen(xinfo.display));
	//buffer = XCreatePixmap(xinfo.display, xinfo.window, hints.width, hints.height, depth);

    // Tell the window manager what input events you want.
    // ButtomMotionMask: The client application receives MotionNotify events only when at least one button is pressed.
    XSelectInput( xinfo.display, xinfo.window, KeyPressMask);
    XMapRaised( xinfo.display, xinfo.window );
    XFlush(xinfo.display);

    sleep(1);
    return dList;
}

int main(int argc, char *argv[]){
	if(argc == 2){
		istringstream iss(argv[1]);
		iss >> fps;
	}

	XInfo xinfo;
	list<Displayable*> list = initX(argc, argv, xinfo);
	eventloop(xinfo, list);
}