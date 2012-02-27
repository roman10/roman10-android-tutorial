SpriteMethodTest 1.0

Author: Chris Pruett

INTRODUCTION

SpriteMethodTest is a simple application for comparing the relative speeds of
various 2D drawing methods on Android.  The current version can test drawing
with the CPU-based Canvas system, or via three different OpenGL ES rendering
methods.

The goal of this program is to provide an example of how to architect an 
efficient 2D rendering pipeline with the Android SDK, and to show the
relative merits of each rendering method.  It is not an absolute benchmark and
should not be used as such.  However, it should give you a good idea of how to
go about making a fast 2D program.

RENDERING METHODS

There are four basic methods that SpriteMethodTest can test.

1) Canvas.  This test draws sprites using Bitmap objects onto a Canvas surface.
This is an entirely CPU-driven operation.  It is also the simplest to code.

2) OpenGL ES flavors:

  2a) Basic Vert Quads.  This is a test of drawing sprites on a plane using 
  basic vertex quads and orthographic projection.  This test requires only the
  baseline OpenGL ES 1.0, which is guaranteed to be supported on all Android
  devices.
  
  2b) Draw Texture Extension.  This test uses the GL_OES_draw_texture extension
  to draw sprites on the screen without any sort of projection or vertex buffers
  involved.  Note that this functionality is not guaranteed to be supported on
  all Android devices, and application authors who use it should check the
  contents of glGetString(GL_EXTENSIONS) before using it.  This extension is
  currently supported by the T-Mobile G1.
  
  2c) VBO Extension.  This test is fundamentally the same as 2a except that the
  vertex arrays describing the sprites are stored in vertex buffer objects,
  which means that they reside in the GPU's memory rather than in main memory.
  As with 2b, this functionality is supported on the G1 but is not guaranteed
  to be supported on other devices; the extension string must be checked at
  runtime before you use it.

INTERPRETING RESULTS

After a test is run, a dialog containing some basic timing results will be
displayed.  This dialog contains four numbers:

  - Frame Time.  This is the time (in ms and frames per second) that each frame
  took, on average, to draw from beginning to end.  This is the most valuable
  part of the result.
  
  - Draw Time.  This is the time that was spent, on average, making draw calls
  against the OpenGL ES or Canvas API.  Under Canvas this is mostly just the
  time that Bitmap.drawCanvas() required.  Under OpenGL ES, this time is the time
  it took to issue all GL commands for a single frame.
  
  - Page Flip Time.  This is a slightly misleading statistic.  It tracks the
  amount of time that the rendering thread spent waiting for the commands 
  issued during Draw to actually appear on the screen.  Under the Canvas test
  this time is defined by the time spent in lockCanvas() and 
  unlockCanvasAndPost().  Under Open GL ES this is the time spent in 
  eglSwapBuffers().  This statistic is important because it shows how long
  your rendering thread may block while waiting for the previous frame to 
  complete.  If this value is significant, you might want to move some 
  non-rendering code into another thread.
  
  - Sim Time.  This is the time spent running the "simulation" step--the code
  that moves the robot sprites around on the screen.  If you uncheck the
  "animate" option, this time will always be zero.
  
DESIGN NOTES

All of the tests follow the same basic pattern: an activity to run the test
starts up and spawns a separate thread to draw to the activity's surface.  This
thread acquires a surface holder object and then draws into it as fast as
it can.  This pattern is based on the SpriteText example from the API Demos
sample, but it has been modified to work with both Canvas and OpenGL ES.

FURTHER OPTIMIZATIONS

This is not an absolutely optimal 2D drawing implementation, but it's fairly
simple and produces good results. Other things that could be done to improve
performance include better management of image and canvas bitmaps (or, in the
OpenGL ES case, texture compression is an option on the G1).  The canvas is
redrawn from scratch every frame, which probably isn't necessary; a
dirty rectangle-based system might improve results.  There are other OpenGL ES
extensions that might be applicable (such as point sprites).  The simulation
code could easily be moved into another thread so that it can run while the
rendering thread blocks on page flip.  And fixed point math could produce an 
edge over floating point math, especially when there are a large number of
sprites on the screen.

TAKEAWAY

Here are some general observations about writing efficient 2D (and 3D) rendering
systems on Android.

  - Canvas is super easy to use but has a low upper bound for performance.
  Though not tested in this example, rotation and scaling sprites using Canvas
  is probably very expensive.  Still, for a Tetris or color matching game, or
  even a top-down RPG that does not require 60fps, Canvas may be enough.
  
  - OpenGL ES is the way to go for 2D applications that need to maintain a high
  frame rate.  It's also the way to go if scaling or rotation are required, as
  these features are almost free.
  
  - OpenGL ES extensions, in particular the Draw Texture extension, can produce
  very good results on the G1.  However, not all devices are guaranteed to have
  these extensions, so your code should be built to handle differing rendering
  systems.  Hopefully this code shows how the same rendering pipeline and 
  simulation code can be used with a variety of different rendering methods.
  
  - When drawing static vertex arrays (whether for 2D or 3D) on the G1, the
  vertex buffer object extension is a big win.  You should use it.
  
  - JNI call overhead is something to think about with OpenGL ES.  Each GL call
  will have to jump through JNI to get to the actual C++ implementation, and
  after a while that overhead may add up.  You might want to test drawing
  methods that are slower on their own but result in fewer total GL calls;
  for example, a tile-based game might be faster when rendered as a vertex grid
  (using the VBO extension) than as individual draw texture calls for each tile.
  This sample suggests that the draw texture extension is the fastest way to
  draw 2D sprites on the G1, but each draw call also requires at least one GL
  call, so in the case of a grid a vertex array might actually be faster.
  
  - One thing that this test does implicitly is avoid all allocation at runtime
  once the test has begun.  It invokes the GC before the test starts and then
  makes sure not to allocate memory explicitly (Java often allocates memory
  under the hood) so that the GC will not interfere with the test.  On the G1
  the GC takes between 100ms and 300ms to run, so for performance applications
  you *really* want to avoid allocating memory outside of safe pause states.