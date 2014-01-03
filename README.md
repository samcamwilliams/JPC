JPC
===

JPC is a keyboard driven MPD client written in Java, taking elements of VI (for example, modality) and emacs (L/MISP). I have been using it as my only media player for ~2.5 years.

Features:
* Entirely keyboard driven - no need for a mouse at all.
* LISP implementation (MISP - Media-ISP) allowing arbitary computation.
* Minimal and clean UI that still functions well when occupying only one twelth of a 1680 * 1050 monitor (420 * 350 pixels). Perfect for use with a tiling window manager!
* Dual modes of operation - command and insert. Command uses bound key patterns to execute operations quickly and insert allows entering of complex and arbitary MISP expressions.
* Multiple buffer support.
* Fully customisable keyboard bindings.
* Regular expressions (with selection groups) for command mode key bindings, allowing quantifiers and conditionals using substitutions. For example, '40v' may set the volume to 40. '+10v' may also increase the volume by 10.
* Trigger execution of any code at certain points in execution (for example, after a track finishes).
* View and remap key bindings in live sessions.
* View defined functions (and their definition in MISP) in live sessions.
* Connection loss recovery.
* Command autocomplete (currently quite basic).
* _(Tests - if you can call that a feature!)_

JPC is currently fairly stable and well featured, but certain features are missing (for example, if the MPD server is not found on localhost at port 6600, the program must be recompiled). Code (and documentation!) contributions greatly appreciated and accepted. JPC is currently developed sporadically, when I have time.

## Usage ##

* Build with netbeans.
* Install the executable as you see fit (I have it bound to a key combination in i3). The `LIB` directory needs to be in the same directory as the executable.
* Consider reading some of the files from the library to get a feel for MISP and the default key bindings.

At some point before a real 'milestone' release, I intend to fully document the system.

## Future Work ##

* Documentation! MISP and the general work flow need to be documented extensively.
* Configuration of options through MISP.
* Fix minor stability and speed issues and prepare for a point release.
