/*
 * Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * @author Masato Watanabe
 */
package jp.mwsoft.mecabhelper.util

import java.io.File

import org.apache.commons.io.{ FilenameUtils, IOCase }

trait CommonsFilenameUtils {

  protected val that: File

  /**
   * Normalizes a path, removing double and single dot path steps.
   * <p>
   * This method normalizes a path to a standard format.
   * The input may contain separators in either Unix or Windows format.
   * The output will contain separators in the format specified.
   * <p>
   * A trailing slash will be retained.
   * A double slash will be merged to a single slash (but UNC names are handled).
   * A single dot path segment will be removed.
   * A double dot will cause that path segment and the one before to be removed.
   * If the double dot has no parent path segment to work with, <code>null</code>
   * is returned.
   * <p>
   * The output will be the same on both Unix and Windows except
   * for the separator character.
   * <pre>
   * /foo//               -->   /foo/
   * /foo/./              -->   /foo/
   * /foo/../bar          -->   /bar
   * /foo/../bar/../baz   -->   /baz
   * //foo//./bar         -->   /foo/bar
   * /../                 -->   null
   * ../foo               -->   null
   * foo/bar/..           -->   foo/
   * foo/../../bar        -->   null
   * foo/../bar           -->   bar
   * //server/foo/../bar  -->   //server/bar
   * //server/../bar      -->   null
   * C:\foo\..\bar        -->   C:\bar
   * C:\..\bar            -->   null
   * ~/../bar             -->   null
   * </pre>
   * The output will be the same on both Unix and Windows including
   * the separator character.
   *
   * @param unixSeparator <code>true</code> if a unix separator should
   * be used or <code>false</code> if a windows separator should be used.
   * @return the normalized filename, or null if invalid
   */
  def normalizePath(unixSeparator: Boolean): String = {
    FilenameUtils.normalize(that.getPath, unixSeparator)
  }

  /**
   * Normalizes a path, removing double and single dot path steps.
   * <p>
   * This method normalizes a path to a standard format.
   * The input may contain separators in either Unix or Windows format.
   * The output will contain separators in the format specified.
   * <p>
   * A trailing slash will be retained.
   * A double slash will be merged to a single slash (but UNC names are handled).
   * A single dot path segment will be removed.
   * A double dot will cause that path segment and the one before to be removed.
   * If the double dot has no parent path segment to work with, <code>null</code>
   * is returned.
   * <p>
   * The output will be the same on both Unix and Windows except
   * for the separator character.
   * <pre>
   * /foo/../bar          -->   /bar
   * /foo/../bar/../baz   -->   /baz
   * //foo//./bar         -->   /foo/bar
   * /../                 -->   null
   * ../foo               -->   null
   * foo/bar/..           -->   foo/
   * foo/../../bar        -->   null
   * foo/../bar           -->   bar
   * //server/foo/../bar  -->   //server/bar
   * //server/../bar      -->   null
   * C:\foo\..\bar        -->   C:\bar
   * C:\..\bar            -->   null
   * ~/../bar             -->   null
   * </pre>
   * The output will be the same on both Unix and Windows including
   * the separator character.
   *
   * @return the normalized filename, or null if invalid
   */
  def normalizePath: String = FilenameUtils.normalize(that.getPath)

  /**
   * Concatenates a filename to a base path using normal command line style rules.
   * <p>
   * The effect is equivalent to resultant directory after changing
   * directory to the first argument, followed by changing directory to
   * the second argument.
   * <p>
   * The first argument is the base path, the second is the path to concatenate.
   * The returned path is always normalized via {@link #normalize(String)},
   * thus <code>..</code> is handled.
   * <p>
   * If <code>pathToAdd</code> is absolute (has an absolute prefix), then
   * it will be normalized and returned.
   * Otherwise, the paths will be joined, normalized and returned.
   * <p>
   * The output will be the same on both Unix and Windows except
   * for the separator character.
   * <pre>
   * /foo/ + bar          -->   /foo/bar
   * /foo + bar           -->   /foo/bar
   * /foo + /bar          -->   /bar
   * /foo + C:/bar        -->   C:/bar
   * /foo + C:bar         -->   C:bar (*)
   * /foo/a/ + ../bar     -->   foo/bar
   * /foo/ + ../../bar    -->   null
   * /foo/ + /bar         -->   /bar
   * /foo/.. + /bar       -->   /bar
   * /foo + bar/c.txt     -->   /foo/bar/c.txt
   * /foo/c.txt + bar     -->   /foo/c.txt/bar (!)
   * </pre>
   * (*) Note that the Windows relative drive prefix is unreliable when
   * used with this method.
   * (!) Note that the first parameter must be a path. If it ends with a name, then
   * the name will be built into the concatenated path. If this might be a problem,
   * use {@link #getFullPath(String)} on the base path argument.
   *
   * @param fullFilenameToAdd  the filename (or path) to attach to the base
   * @return the concatenated path, or null if invalid
   */
  def concatPath(fullFilenameToAdd: String): String = {
    FilenameUtils.concat(that.getPath, fullFilenameToAdd)
  }

  /**
   * Concatenates a filename to a base path using normal command line style rules.
   * <p>
   * The effect is equivalent to resultant directory after changing
   * directory to the first argument, followed by changing directory to
   * the second argument.
   * <p>
   * The first argument is the base path, the second is the path to concatenate.
   * The returned path is always normalized via {@link #normalize(String)},
   * thus <code>..</code> is handled.
   * <p>
   * If <code>pathToAdd</code> is absolute (has an absolute prefix), then
   * it will be normalized and returned.
   * Otherwise, the paths will be joined, normalized and returned.
   * <p>
   * The output will be the same on both Unix and Windows except
   * for the separator character.
   * <pre>
   * /foo/ + bar          -->   /foo/bar
   * /foo + bar           -->   /foo/bar
   * /foo + /bar          -->   /bar
   * /foo + C:/bar        -->   C:/bar
   * /foo + C:bar         -->   C:bar (*)
   * /foo/a/ + ../bar     -->   foo/bar
   * /foo/ + ../../bar    -->   null
   * /foo/ + /bar         -->   /bar
   * /foo/.. + /bar       -->   /bar
   * /foo + bar/c.txt     -->   /foo/bar/c.txt
   * /foo/c.txt + bar     -->   /foo/c.txt/bar (!)
   * </pre>
   * (*) Note that the Windows relative drive prefix is unreliable when
   * used with this method.
   * (!) Note that the first parameter must be a path. If it ends with a name, then
   * the name will be built into the concatenated path. If this might be a problem,
   * use {@link #getFullPath(String)} on the base path argument.
   *
   * @param fullFilenameToAdd  the filename (or path) to attach to the base
   * @return the concatenated path, or null if invalid
   */
  def concatPath(fullFilenameToAdd: File): String = {
    FilenameUtils.concat(that.getPath, fullFilenameToAdd.getPath)
  }

  /**
   * Concatenates a filename to a base path using normal command line style rules.
   * <p>
   * The effect is equivalent to resultant directory after changing
   * directory to the first argument, followed by changing directory to
   * the second argument.
   * <p>
   * The first argument is the base path, the second is the path to concatenate.
   * The returned path is always normalized via {@link #normalize(String)},
   * thus <code>..</code> is handled.
   * <p>
   * If <code>pathToAdd</code> is absolute (has an absolute prefix), then
   * it will be normalized and returned.
   * Otherwise, the paths will be joined, normalized and returned.
   * <p>
   * The output will be the same on both Unix and Windows except
   * for the separator character.
   * <pre>
   * /foo/ + bar          -->   /foo/bar
   * /foo + bar           -->   /foo/bar
   * /foo + /bar          -->   /bar
   * /foo + C:/bar        -->   C:/bar
   * /foo + C:bar         -->   C:bar (*)
   * /foo/a/ + ../bar     -->   foo/bar
   * /foo/ + ../../bar    -->   null
   * /foo/ + /bar         -->   /bar
   * /foo/.. + /bar       -->   /bar
   * /foo + bar/c.txt     -->   /foo/bar/c.txt
   * /foo/c.txt + bar     -->   /foo/c.txt/bar (!)
   * </pre>
   * (*) Note that the Windows relative drive prefix is unreliable when
   * used with this method.
   * (!) Note that the first parameter must be a path. If it ends with a name, then
   * the name will be built into the concatenated path. If this might be a problem,
   * use {@link #getFullPath(String)} on the base path argument.
   *
   * @param fullFilenameToAdd  the filename (or path) to attach to the base
   * @return the concatenated path, or null if invalid
   */
  def concatPathFile(fullFilenameToAdd: File): File = {
    new File(FilenameUtils.concat(that.getPath, fullFilenameToAdd.getPath))
  }

  /**
   * Concatenates a filename to a base path using normal command line style rules.
   * <p>
   * The effect is equivalent to resultant directory after changing
   * directory to the first argument, followed by changing directory to
   * the second argument.
   * <p>
   * The first argument is the base path, the second is the path to concatenate.
   * The returned path is always normalized via {@link #normalize(String)},
   * thus <code>..</code> is handled.
   * <p>
   * If <code>pathToAdd</code> is absolute (has an absolute prefix), then
   * it will be normalized and returned.
   * Otherwise, the paths will be joined, normalized and returned.
   * <p>
   * The output will be the same on both Unix and Windows except
   * for the separator character.
   * <pre>
   * /foo/ + bar          -->   /foo/bar
   * /foo + bar           -->   /foo/bar
   * /foo + /bar          -->   /bar
   * /foo + C:/bar        -->   C:/bar
   * /foo + C:bar         -->   C:bar (*)
   * /foo/a/ + ../bar     -->   foo/bar
   * /foo/ + ../../bar    -->   null
   * /foo/ + /bar         -->   /bar
   * /foo/.. + /bar       -->   /bar
   * /foo + bar/c.txt     -->   /foo/bar/c.txt
   * /foo/c.txt + bar     -->   /foo/c.txt/bar (!)
   * </pre>
   * (*) Note that the Windows relative drive prefix is unreliable when
   * used with this method.
   * (!) Note that the first parameter must be a path. If it ends with a name, then
   * the name will be built into the concatenated path. If this might be a problem,
   * use {@link #getFullPath(String)} on the base path argument.
   *
   * @param fullFilenameToAdd  the filename (or path) to attach to the base
   * @return the concatenated path, or null if invalid
   */
  def concatPathFile(fullFilenameToAdd: String): File = {
    new File(FilenameUtils.concat(that.getPath, fullFilenameToAdd))
  }

  /**
   * Converts all separators to the Unix separator of forward slash.
   *
   * @return the updated path
   */
  def separatorsToUnix: String = FilenameUtils.separatorsToUnix(that.getPath)

  /**
   * Converts all separators to the Windows separator of backslash.
   *
   * @return the updated path
   */
  def separatorsToWindows: String = FilenameUtils.separatorsToWindows(that.getPath)

  /**
   * Converts all separators to the Windows separator of backslash.
   *
   * @return the updated path
   */
  def separatorsToSystem: String = FilenameUtils.separatorsToSystem(that.getPath)

  /**
   * Returns the length of the filename prefix, such as <code>C:/</code> or <code>~/</code>.
   * <p>
   * This method will handle a file in either Unix or Windows format.
   * <p>
   * The prefix length includes the first slash in the full filename
   * if applicable. Thus, it is possible that the length returned is greater
   * than the length of the input string.
   * <pre>
   * Windows:
   * a\b\c.txt           --> ""          --> relative
   * \a\b\c.txt          --> "\"         --> current drive absolute
   * C:a\b\c.txt         --> "C:"        --> drive relative
   * C:\a\b\c.txt        --> "C:\"       --> absolute
   * \\server\a\b\c.txt  --> "\\server\" --> UNC
   *
   * Unix:
   * a/b/c.txt           --> ""          --> relative
   * /a/b/c.txt          --> "/"         --> absolute
   * ~/a/b/c.txt         --> "~/"        --> current user
   * ~                   --> "~/"        --> current user (slash added)
   * ~user/a/b/c.txt     --> "~user/"    --> named user
   * ~user               --> "~user/"    --> named user (slash added)
   * </pre>
   * <p>
   * The output will be the same irrespective of the machine that the code is running on.
   * ie. both Unix and Windows prefixes are matched regardless.
   *
   * @return the length of the prefix, -1 if invalid or null
   */
  def prefixLength: Int = FilenameUtils.getPrefixLength(that.getPath)

  /**
   * Returns the index of the last directory separator character.
   * <p>
   * This method will handle a file in either Unix or Windows format.
   * The position of the last forward or backslash is returned.
   * <p>
   * The output will be the same irrespective of the machine that the code is running on.
   *
   * @return the index of the last separator character, or -1 if there
   * is no such character
   */
  def indexOfLastSeparator: Int = FilenameUtils.indexOfLastSeparator(that.getPath)

  /**
   * Returns the index of the last extension separator character, which is a dot.
   * <p>
   * This method also checks that there is no directory separator after the last dot.
   * To do this it uses {@link #indexOfLastSeparator(String)} which will
   * handle a file in either Unix or Windows format.
   * <p>
   * The output will be the same irrespective of the machine that the code is running on.
   *
   * @param filename  the filename to find the last path separator in, null returns -1
   * @return the index of the last separator character, or -1 if there
   * is no such character
   */
  def indexOfExtension: Int = FilenameUtils.indexOfExtension(that.getPath)

  /**
   * Gets the prefix from a full filename, such as <code>C:/</code>
   * or <code>~/</code>.
   * <p>
   * This method will handle a file in either Unix or Windows format.
   * The prefix includes the first slash in the full filename where applicable.
   * <pre>
   * Windows:
   * a\b\c.txt           --> ""          --> relative
   * \a\b\c.txt          --> "\"         --> current drive absolute
   * C:a\b\c.txt         --> "C:"        --> drive relative
   * C:\a\b\c.txt        --> "C:\"       --> absolute
   * \\server\a\b\c.txt  --> "\\server\" --> UNC
   *
   * Unix:
   * a/b/c.txt           --> ""          --> relative
   * /a/b/c.txt          --> "/"         --> absolute
   * ~/a/b/c.txt         --> "~/"        --> current user
   * ~                   --> "~/"        --> current user (slash added)
   * ~user/a/b/c.txt     --> "~user/"    --> named user
   * ~user               --> "~user/"    --> named user (slash added)
   * </pre>
   * <p>
   * The output will be the same irrespective of the machine that the code is running on.
   * ie. both Unix and Windows prefixes are matched regardless.
   *
   * @return the prefix of the file, null if invalid
   */
  def getPrefix: String = FilenameUtils.getPrefix(that.getPath)

  /**
   * Gets the base name, minus the full path and extension, from a full filename.
   * <p>
   * This method will handle a file in either Unix or Windows format.
   * The text after the last forward or backslash and before the last dot is returned.
   * <pre>
   * a/b/c.txt --> c
   * a.txt     --> a
   * a/b/c     --> c
   * </pre>
   * <p>
   * The output will be the same irrespective of the machine that the code is running on.
   *
   * @return the name of the file without the path, or an empty string if none exists
   */
  def baseName: String = FilenameUtils.getBaseName(that.getPath)

  /**
   * Gets the extension of a filename.
   * <p>
   * This method returns the textual part of the filename after the last dot.
   * There must be no directory separator after the dot.
   * <pre>
   * foo.txt      --> "txt"
   * a/b/c.jpg    --> "jpg"
   * a/b.txt/c    --> ""
   * a/b/c        --> ""
   * </pre>
   * <p>
   * The output will be the same irrespective of the machine that the code is running on.
   *
   * @return the extension of the file or an empty string if none exists or <code>null</code>
   * if the filename is <code>null</code>.
   */
  def extension: String = FilenameUtils.getExtension(that.getPath)

  /**
   * Removes the extension from a filename.
   * <p>
   * This method returns the textual part of the filename before the last dot.
   * There must be no directory separator after the dot.
   * <pre>
   * foo.txt    --> foo
   * a\b\c.jpg  --> a\b\c
   * a\b\c      --> a\b\c
   * a.b\c      --> a.b\c
   * </pre>
   * <p>
   * The output will be the same irrespective of the machine that the code is running on.
   *
   * @return the filename minus the extension
   */
  def removeExtensionPath: String = FilenameUtils.removeExtension(that.getPath)

  /**
   * Checks whether two filenames are equal after both have been normalized
   * and using the case rules of the system.
   * <p>
   * Both filenames are first passed to {@link #normalize(String)}.
   * The check is then performed case-sensitive on Unix and
   * case-insensitive on Windows.
   *
   * @param file  the second filename to query, may be null
   * @return true if the filenames are equal, null equals null
   */
  def equalPath(file: File): Boolean = FilenameUtils.equalsNormalizedOnSystem(that.getPath, file.getPath)

  /**
   * Checks whether two filenames are equal after both have been normalized
   * and using the case rules of the system.
   * <p>
   * Both filenames are first passed to {@link #normalize(String)}.
   * The check is then performed case-sensitive on Unix and
   * case-insensitive on Windows.
   *
   * @param file  the second filename to query, may be null
   * @return true if the filenames are equal, null equals null
   */
  def equalPath(file: String): Boolean = FilenameUtils.equalsNormalizedOnSystem(that.getPath, file)

  /**
   * Checks whether two filenames are equal, optionally normalizing and providing
   * control over the case-sensitivity.
   *
   * @param file  the second filename to query, may be null
   * @param normalized  whether to normalize the filenames
   * @param caseSensitivity  what case sensitivity rule to use, null means case-sensitive
   * @return true if the filenames are equal, null equals null
   */
  def equalPath(file: File, normalized: Boolean, caseSensitivity: IOCase = IOCase.SYSTEM): Boolean = {
    FilenameUtils.equals(that.getPath, file.getPath, normalized, caseSensitivity)
  }

  /**
   * Checks whether the extension of the filename is that specified.
   * <p>
   * This method obtains the extension as the textual part of the filename
   * after the last dot. There must be no directory separator after the dot.
   * The extension check is case-sensitive on all platforms.
   *
   * @param extension  the extension to check for, null or empty checks for no extension
   * @return true if the filename has the specified extension
   */
  def isExtension(extension: String): Boolean = FilenameUtils.isExtension(that.getPath, extension)

  /**
   * Checks whether the extension of the filename is one of those specified.
   * <p>
   * This method obtains the extension as the textual part of the filename
   * after the last dot. There must be no directory separator after the dot.
   * The extension check is case-sensitive on all platforms.
   *
   * @param extensions  the extensions to check for, null checks for no extension
   * @return true if the filename is one of the extensions
   */
  def isExtension(extensions: Seq[String]): Boolean = {
    import scala.collection.JavaConversions
    FilenameUtils.isExtension(that.getPath, JavaConversions.asJavaCollection(extensions))
  }

  /**
   * Checks a filename to see if it matches the specified wildcard matcher
   * using the case rules of the system.
   * <p>
   * The wildcard matcher uses the characters '?' and '*' to represent a
   * single or multiple (zero or more) wildcard characters.
   * This is the same as often found on Dos/Unix command lines.
   * The check is case-sensitive on Unix and case-insensitive on Windows.
   * <pre>
   * wildcardMatch("c.txt", "*.txt")      --> true
   * wildcardMatch("c.txt", "*.jpg")      --> false
   * wildcardMatch("c.txt", "*.???")      --> true
   * wildcardMatch("c.txt", "*.????")     --> false
   * </pre>
   * N.B. the sequence "*?" does not work properly at present in match strings.
   *
   * @param matcher  the wildcard string to match against
   * @return true if the filename matches the wilcard string
   */
  def pathMatches(matcher: String): Boolean = FilenameUtils.wildcardMatch(that.getPath, matcher, IOCase.SYSTEM)

  /**
   * Checks a filename to see if it matches the specified wildcard matcher
   * allowing control over case-sensitivity.
   * <p>
   * The wildcard matcher uses the characters '?' and '*' to represent a
   * single or multiple (zero or more) wildcard characters.
   * N.B. the sequence "*?" does not work properly at present in match strings.
   *
   * @param wildcardMatcher  the wildcard string to match against
   * @param caseSensitivity  what case sensitivity rule to use, null means case-sensitive
   * @return true if the filename matches the wilcard string
   */
  def pathMatches(matcher: String, caseSensitivity: IOCase): Boolean = {
    FilenameUtils.wildcardMatch(that.getPath, matcher, caseSensitivity)
  }

}