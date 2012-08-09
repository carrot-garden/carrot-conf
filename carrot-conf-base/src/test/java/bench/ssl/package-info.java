/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
/**
 * 
 * http://stackoverflow.com/questions/7615645/ssl-handshake-alert-unrecognized-name-error-since-upgrade-to-java-1-7-0
 * 
 * This led me to look at my Apache config files and I found that if I added a ServerName or ServerAlias for the name sent from the client/java side, it worked correctly without any errors.
 *	
 * <VirtualHost mydomain.com:443>
 * ServerName mydomain.com
 * ServerAlias www.mydomain.com
 * 
 */
package bench.ssl;

