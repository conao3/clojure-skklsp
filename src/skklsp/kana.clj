(ns skklsp.kana)

(defn compile [rule]
  ((:compiler rule) rule))

(defn table-compiler [rule]
  (->> rule
       :table
       (mapcat (fn [[k val]]
                 (map (fn [f v] [(str f k) v]) (:first rule) val)))
       (filter second)
       (into {})))

(def basic-rule
  {:compiler table-compiler
   :first ["" "k" "s" "t" "n" "h" "f" "m" "y" "r" "w"]
   :table {"a" ["あ" "か" "さ" "た" "な" "は" "ふぁ" "ま" "や" "ら" "わ"]
           "i" ["い" "き" "し" "ち" "に" "ひ" "ふぃ" "み" nil "り" nil]
           "u" ["う" "く" "す" "つ" "ぬ" "ふ" "ふ" "む" "ゆ" "る" nil]
           "e" ["え" "け" "せ" "て" "ね" "へ" "ふぇ" "め" nil "れ" nil]
           "o" ["お" "こ" "そ" "と" "の" "ほ" "ふぉ" "も" "よ" "ろ" "を"]}})

(def rule-b
  {:branch ["z" "n" "k" "j" "d" "l"]
   :table {"k" ["かん" "かん" "きん" "くん" "けん" "こん"]
           "s" ["さん" "さん" "しん" "すん" "せん" "そん"]
           "t" ["たん" "たん" "ちん" "つん" "てん" "とん"]
           "n" ["なん" "ん" "にん" "ぬん" "ねん" "のん"]
           "h" ["はん" "はん" "ひん" "ふん" "へん" "ほん"]
           "f" ["ふぁん" "ふぁん" "ふぃん" "ふん" "ふぇん" "ふぉん"]
           "m" ["まん" nil "みん" "むん" "めん" "もん"]
           "y" ["やん" "やん" nil "ゆん" nil "よん"]
           "r" ["らん" "らん" "りん" "るん" "れん" "ろん"]
           "w" ["わん" "わん" "うぃん" nil "うぇん" "うぉん"]}})

;; This buffer is for Clojure experiments and evaluation.

;; Press C-j to evaluate the last expression.

;; You can also press C-u C-j to evaluate the expression and pretty-print its result.

(def s-server (skklsp.core/start-server 12341))
#'user/s-server

(def buffer (java.nio.ByteBuffer/allocate 10240))
#'user/buffer
(def selector (:selector s-server))
#'user/selector
(def server-socket (:server-socket s-server))
#'user/server-socket


(. selector select)
1

(def selected-keys (. selector selectedKeys))
#'user/selected-keys

(def key (first (seq selected-keys)))
#'user/key

(. selected-keys remove key)
true

(let [^SocketChannel client-socket (. key channel)
      bytes-read (. client-socket read buffer)]
  (if (neg? bytes-read)
    (do
      (println "Connection Closed" (str (. client-socket getRemoteAddress)))
      (. key cancel)
      (. client-socket close))
    (do
      (. buffer flip)
      ;; (println "Buffer" (io/reader (byte-array (. buffer array))))
      ;; (. client-socket write buffer)
      ;; (. buffer clear)
      )))
#object[java.nio.HeapByteBuffer 0x69a2d150 "java.nio.HeapByteBuffer[pos=0 lim=2532 cap=10240]"]

(str buffer)
"java.nio.HeapByteBuffer[pos=0 lim=2532 cap=10240]"

(String. (. buffer array))
"Content-Length: 2508\r\n\r\n{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"initialize\",\"params\":{\"processId\":null,\"clientInfo\":{\"name\":\"Eglot\",\"version\":\"1.17\"},\"rootPath\":\"/home/conao/dev/tmp/txt/\",\"rootUri\":\"file:///home/conao/dev/tmp/txt\",\"initializationOptions\":{},\"capabilities\":{\"workspace\":{\"applyEdit\":true,\"executeCommand\":{\"dynamicRegistration\":false},\"workspaceEdit\":{\"documentChanges\":true},\"didChangeWatchedFiles\":{\"dynamicRegistration\":true},\"symbol\":{\"dynamicRegistration\":false},\"configuration\":true,\"workspaceFolders\":true},\"textDocument\":{\"synchronization\":{\"dynamicRegistration\":false,\"willSave\":true,\"willSaveWaitUntil\":true,\"didSave\":true},\"completion\":{\"dynamicRegistration\":false,\"completionItem\":{\"snippetSupport\":false,\"deprecatedSupport\":true,\"resolveSupport\":{\"properties\":[\"documentation\",\"details\",\"additionalTextEdits\"]},\"tagSupport\":{\"valueSet\":[1]}},\"contextSupport\":true},\"hover\":{\"dynamicRegistration\":false,\"contentFormat\":[\"markdown\",\"plaintext\"]},\"signatureHelp\":{\"dynamicRegistration\":false,\"signatureInformation\":{\"parameterInformation\":{\"labelOffsetSupport\":true},\"documentationFormat\":[\"markdown\",\"plaintext\"],\"activeParameterSupport\":true}},\"references\":{\"dynamicRegistration\":false},\"definition\":{\"dynamicRegistration\":false,\"linkSupport\":true},\"declaration\":{\"dynamicRegistration\":false,\"linkSupport\":true},\"implementation\":{\"dynamicRegistration\":false,\"linkSupport\":true},\"typeDefinition\":{\"dynamicRegistration\":false,\"linkSupport\":true},\"documentSymbol\":{\"dynamicRegistration\":false,\"hierarchicalDocumentSymbolSupport\":true,\"symbolKind\":{\"valueSet\":[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26]}},\"documentHighlight\":{\"dynamicRegistration\":false},\"codeAction\":{\"dynamicRegistration\":false,\"resolveSupport\":{\"properties\":[\"edit\",\"command\"]},\"dataSupport\":true,\"codeActionLiteralSupport\":{\"codeActionKind\":{\"valueSet\":[\"quickfix\",\"refactor\",\"refactor.extract\",\"refactor.inline\",\"refactor.rewrite\",\"source\",\"source.organizeImports\"]}},\"isPreferredSupport\":true},\"formatting\":{\"dynamicRegistration\":false},\"rangeFormatting\":{\"dynamicRegistration\":false},\"rename\":{\"dynamicRegistration\":false},\"inlayHint\":{\"dynamicRegistration\":false},\"publishDiagnostics\":{\"relatedInformation\":false,\"codeDescriptionSupport\":false,\"tagSupport\":{\"valueSet\":[1,2]}}},\"window\":{\"showDocument\":{\"support\":true},\"workDoneProgress\":true},\"general\":{\"positionEncodings\":[\"utf-32\",\"utf-8\",\"utf-16\"]},\"experimental\":{}},\"workspaceFolders\":[{\"uri\":\"file:///home/conao/dev/tmp/txt\",\"name\":\"~/dev/tmp/txt/\"}]}}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            "

(. buffer position 0)
#object[java.nio.HeapByteBuffer 0x69a2d150 "java.nio.HeapByteBuffer[pos=0 lim=2532 cap=10240]"]

(. buffer getChar)

(char (. buffer get 0))

(skklsp.subr/byte-buffer-readline buffer)
"Content-Length: 2508"

(skklsp.subr/header-parser buffer)
{:Content-Length "2508"}

(skklsp.subr/byte-buffer-read->string buffer 2508)
"{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"initialize\",\"params\":{\"processId\":null,\"clientInfo\":{\"name\":\"Eglot\",\"version\":\"1.17\"},\"rootPath\":\"/home/conao/dev/tmp/txt/\",\"rootUri\":\"file:///home/conao/dev/tmp/txt\",\"initializationOptions\":{},\"capabilities\":{\"workspace\":{\"applyEdit\":true,\"executeCommand\":{\"dynamicRegistration\":false},\"workspaceEdit\":{\"documentChanges\":true},\"didChangeWatchedFiles\":{\"dynamicRegistration\":true},\"symbol\":{\"dynamicRegistration\":false},\"configuration\":true,\"workspaceFolders\":true},\"textDocument\":{\"synchronization\":{\"dynamicRegistration\":false,\"willSave\":true,\"willSaveWaitUntil\":true,\"didSave\":true},\"completion\":{\"dynamicRegistration\":false,\"completionItem\":{\"snippetSupport\":false,\"deprecatedSupport\":true,\"resolveSupport\":{\"properties\":[\"documentation\",\"details\",\"additionalTextEdits\"]},\"tagSupport\":{\"valueSet\":[1]}},\"contextSupport\":true},\"hover\":{\"dynamicRegistration\":false,\"contentFormat\":[\"markdown\",\"plaintext\"]},\"signatureHelp\":{\"dynamicRegistration\":false,\"signatureInformation\":{\"parameterInformation\":{\"labelOffsetSupport\":true},\"documentationFormat\":[\"markdown\",\"plaintext\"],\"activeParameterSupport\":true}},\"references\":{\"dynamicRegistration\":false},\"definition\":{\"dynamicRegistration\":false,\"linkSupport\":true},\"declaration\":{\"dynamicRegistration\":false,\"linkSupport\":true},\"implementation\":{\"dynamicRegistration\":false,\"linkSupport\":true},\"typeDefinition\":{\"dynamicRegistration\":false,\"linkSupport\":true},\"documentSymbol\":{\"dynamicRegistration\":false,\"hierarchicalDocumentSymbolSupport\":true,\"symbolKind\":{\"valueSet\":[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26]}},\"documentHighlight\":{\"dynamicRegistration\":false},\"codeAction\":{\"dynamicRegistration\":false,\"resolveSupport\":{\"properties\":[\"edit\",\"command\"]},\"dataSupport\":true,\"codeActionLiteralSupport\":{\"codeActionKind\":{\"valueSet\":[\"quickfix\",\"refactor\",\"refactor.extract\",\"refactor.inline\",\"refactor.rewrite\",\"source\",\"source.organizeImports\"]}},\"isPreferredSupport\":true},\"formatting\":{\"dynamicRegistration\":false},\"rangeFormatting\":{\"dynamicRegistration\":false},\"rename\":{\"dynamicRegistration\":false},\"inlayHint\":{\"dynamicRegistration\":false},\"publishDiagnostics\":{\"relatedInformation\":false,\"codeDescriptionSupport\":false,\"tagSupport\":{\"valueSet\":[1,2]}}},\"window\":{\"showDocument\":{\"support\":true},\"workDoneProgress\":true},\"general\":{\"positionEncodings\":[\"utf-32\",\"utf-8\",\"utf-16\"]},\"experimental\":{}},\"workspaceFolders\":[{\"uri\":\"file:///home/conao/dev/tmp/txt\",\"name\":\"~/dev/tmp/txt/\"}]}}"

(:compiler skklsp.kana/basic-rule)

(skklsp.kana/compile skklsp.kana/basic-rule)

(def rule-b)



(def rule-c
  {:branch ["q" "h" "w" "p"]
   :table {"k" ["かい" "くう" "けい" "こう"]
           "s" ["さい" "すう" "せい" "そう"]
           "t" ["たい" "つう" "てい" "とう"]
           "n" ["ない" "ぬう" "ねい" "のう"]
           "h" ["はい" "ふう" "へい" "ほう"]
           "f" ["ふぁい" "ふう" "ふぇい" "ふぉー"]
           "m" ["まい" "むう" "めい" "もう"]
           "y" ["やい" "ゆう" nil "よう"]
           "r" ["らい" "るう" "れい" "ろう"]
           "w" ["わい" nil nil "うぉー"]}})

(def rule-d)

#'user/rule-a

(->> rule-a
     :table
     (mapcat (fn [[k val]]
               (map (fn [b v] [(str k b) v]) (:branch rule-a) val)))
     (filter second)
     (into {}))
{"wo" "を", "ku" "く", "ne" "ね", "e" "え", "mo" "も", "ro" "ろ", "nu" "ぬ", "ya" "や", "ki" "き", "si" "し", "mi" "み", "ti" "ち", "sa" "さ", "ma" "ま", "ni" "に", "tu" "つ", "wa" "わ", "hu" "ふ", "ri" "り", "hi" "ひ", "a" "あ", "so" "そ", "ru" "る", "ha" "は", "se" "せ", "i" "い", "mu" "む", "ra" "ら", "ke" "け", "no" "の", "u" "う", "re" "れ", "ho" "ほ", "ko" "こ", "te" "て", "ka" "か", "su" "す", "na" "な", "to" "と", "ta" "た", "he" "へ", "me" "め", "o" "お", "yu" "ゆ", "yo" "よ"}

(def rule-b
  {:compiler })

(def kana-rule (compile basic-rule))
