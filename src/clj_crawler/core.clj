(ns clj-crawler.core
  (:import (javax.swing JFrame JMenuBar JMenu JMenuItem JPanel JLabel JTextField JComboBox JCheckBox
             JSeparator JButton JProgressBar JTable JScrollPane BorderFactory)
           (javax.swing.table DefaultTableModel)
           (java.awt GridBagConstraints GridBagLayout Insets BorderLayout Font)
           (java.awt.event WindowAdapter ActionListener KeyEvent)))

(defn action-exit []
  (System/exit 0))

(defn action-search []
  (System/exit 0))

(defn create-menu []
  (let [menu-bar (JMenuBar.)
        file-menu (JMenu. "File")
        file-exit-menu-item (JMenuItem. "Exit" KeyEvent/VK_X)]
    (.setMnemonic file-menu KeyEvent/VK_F)
    (.addActionListener file-exit-menu-item (proxy [ActionListener] [] (actionPerformed [event] (action-exit))))
    (.add file-menu file-exit-menu-item)
    (.add menu-bar file-menu)
    menu-bar))

(defmacro set-grid! [constraints field value]
  `(set! (. ~constraints ~(symbol (name field)))
         ~(if (keyword? value)
            `(. GridBagConstraints ~(symbol (name value)))
            value)))

;; to test macros on repl (macroexpand-1 ...)
(defmacro grid-bag-layout [container & body]
  (let [c (gensym "c")
        cntr (gensym "cntr")]
    `(let [~c (GridBagConstraints.)
           ~cntr ~container]
       ~@(loop [result '()
                body body]
           (if (empty? body)
             (reverse result)
             (let [expr (first body)]
               (if (keyword? expr)
                 (recur 
                    ;; new result
                    (cons `(set-grid! ~c ~expr ~(second body)) result)
                    ;; new body
                    (next (next body)))
                 (recur 
                    ;; new result
                    (cons `(.add ~cntr ~expr ~c) result)
                    ;; new body
                    (next body)))))))))

(defn create-search-panel []
  (let [panel 
      (doto (JPanel. (GridBagLayout.))
        (grid-bag-layout
          :anchor GridBagConstraints/EAST
          :insets (Insets. 5 5 0 0)
          (JLabel. "Start URL:")
          :fill GridBagConstraints/HORIZONTAL
          :gridwidth GridBagConstraints/REMAINDER
          :insets (Insets. 5 5 0 5)
          (JTextField.)
          :anchor GridBagConstraints/EAST
          :insets (Insets. 5 5 0 0)
          (JLabel. "Max URLs to Crawl:")
          :insets (Insets. 5 5 0 0)
          (doto (JComboBox. (to-array ["50" "100"]))
            (.setEditable true))
          :anchor GridBagConstraints/WEST
          :insets (Insets. 0 10 0 0)
          (JCheckBox. "Limit crawling to Start URL site")
          :gridwidth GridBagConstraints/REMAINDER
          (JLabel.)
          :anchor GridBagConstraints/EAST
          :insets (Insets. 5 5 0 0)
          (JLabel. "Matches Log File:")
          :fill GridBagConstraints/HORIZONTAL
          :gridwidth GridBagConstraints/REMAINDER
          :insets (Insets. 5 5 0 5)
          (JTextField. (str (System/getProperty "user.dir") (System/getProperty "file.separator") "crawler.log"))
          :anchor GridBagConstraints/EAST
          :insets (Insets. 5 5 0 0)
          (JLabel. "Search String:")
          :fill GridBagConstraints/HORIZONTAL
          :insets (Insets. 5 5 0 0)
          :gridwidth 2
          :weightx 1.0
          (JTextField.)
          :insets (Insets. 5 5 0 5)
          :gridwidth GridBagConstraints/REMAINDER
          (JCheckBox. "Case Sensitive")
          :gridwidth GridBagConstraints/REMAINDER
          :insets (Insets. 5 5 5 5)
          (doto (JButton. "Search")
            (.addActionListener (proxy [ActionListener] [] (actionPerformed [event] (action-search)))))
          :fill GridBagConstraints/HORIZONTAL
          :gridwidth GridBagConstraints/REMAINDER
          :insets (Insets. 5 5 5 5)
          (JSeparator.)
          :anchor GridBagConstraints/EAST
          :insets (Insets. 5 5 0 0)
          (JLabel. "Crawling:")
          :fill GridBagConstraints/HORIZONTAL
          :gridwidth GridBagConstraints/REMAINDER
          :insets (Insets. 5 5 0 5)
          (doto (JLabel.)
            (.setFont (.deriveFont (.getFont (JLabel.)) Font/PLAIN)))
          :anchor GridBagConstraints/EAST
          :insets (Insets. 5 5 0 0)
          (JLabel. "Crawled URLs:")
          :fill GridBagConstraints/HORIZONTAL
          :gridwidth GridBagConstraints/REMAINDER
          :insets (Insets. 5 5 0 5)
          (doto (JLabel.)
            (.setFont (.deriveFont (.getFont (JLabel.)) Font/PLAIN)))
          :anchor GridBagConstraints/EAST
          :insets (Insets. 5 5 0 0)
          (JLabel.)
          :fill GridBagConstraints/HORIZONTAL
          :gridwidth GridBagConstraints/REMAINDER
          :insets (Insets. 5 5 0 5)
          (doto (JLabel.)
            (.setFont (.deriveFont (.getFont (JLabel.)) Font/PLAIN)))
          :anchor GridBagConstraints/EAST
          :insets (Insets. 5 5 0 0)
          (JLabel. "Crawling Process:")
          :fill GridBagConstraints/HORIZONTAL
          :gridwidth GridBagConstraints/REMAINDER
          :insets (Insets. 5 5 0 5)
          (doto (JProgressBar.)
            (.setMinimum 0)
            (.setStringPainted true))
          :anchor GridBagConstraints/EAST
          :insets (Insets. 5 5 10 0)
          (JLabel. "Search Matches:")
          :fill GridBagConstraints/HORIZONTAL
          :gridwidth GridBagConstraints/REMAINDER
          :insets (Insets. 5 5 10 5)
          (doto (JLabel.)
            (.setFont (.deriveFont (.getFont (JLabel.)) Font/PLAIN)))))]
    panel))

(defn create-table []
  (JTable. (proxy [DefaultTableModel] [(to-array-2d []) (to-array ["URL"])] (isCellEditable [row, colum] false))))

(defn create-matches-panel []
  (let [panel
    (doto (JPanel.)
      (.setBorder (BorderFactory/createTitledBorder "Matches"))
      (.setLayout (BorderLayout.))
      (.add (JScrollPane. (create-table)) BorderLayout/CENTER))]
    panel))

(defn start []
  (let [frame (JFrame. "Search Crawler")]
    (.setSize frame 600 600)
    (.addWindowListener frame (proxy [WindowAdapter] [] (windowClosing [event] (action-exit))))
    (.setJMenuBar frame (create-menu))
    ;; search panel
    (.setLayout (.getContentPane frame) (BorderLayout.))
    (.add (.getContentPane frame) (create-search-panel) BorderLayout/NORTH)
    (.add (.getContentPane frame) (create-matches-panel) BorderLayout/CENTER)
    (.show frame)))

(defn -main [& args]
  (start))
