import xml.etree.cElementTree as ET
from bs4 import BeautifulSoup

class Article(object):
  def __init__(self, article):
    if (article):
      self.authors = []
      authors = article.find_all("author")
      if authors:
        for author in authors:
          self.authors.append(author.get_text().encode("utf-8"))

      self.title = article.title.get_text().encode("utf-8") if article.title else None
      self.pages = article.pages.get_text().encode("utf-8") if article.pages else None
      self.year = article.year.get_text().encode("utf-8") if article.year else None
      self.volume = article.volume.get_text().encode("utf-8") if article.volume else None
      self.journal = article.title.get_text().encode("utf-8") if article.journal else None
      self.number = article.title.get_text().encode("utf-8")  if article.number else None
      self.url = article.title.get_text().encode("utf-8") if article.url else None
      self.ee = article.title.get_text().encode("utf-8") if article.ee else None

  def read_article(self, article):
    if (article):
      self.authors = []
      authors = article.find_all("author")
      if authors:
        for author in authors:
          self.authors.append(author.get_text().encode("utf-8"))

      self.title = article.title.get_text().encode("utf-8") if article.title else None
      self.pages = article.pages.get_text().encode("utf-8") if article.pages else None
      self.year = article.year.get_text().encode("utf-8") if article.year else None
      self.volume = article.volume.get_text().encode("utf-8") if article.volume else None
      self.journal = article.title.get_text().encode("utf-8") if article.journal else None
      self.number = article.title.get_text().encode("utf-8")  if article.number else None
      self.url = article.title.get_text().encode("utf-8") if article.url else None
      self.ee = article.title.get_text().encode("utf-8") if article.ee else None

  def write_article(self, count):
    
    str_article = ''
    

    if self.authors:
      str_article += str(count) + '|'
      for author in self.authors:
        str_article += author + '^'
    str_article = str_article [:-1]
    str_article += '|' + self.title + '|' if self.title else ""
    str_article += self.pages + '|' if self.pages else ""
    str_article += self.year + '|' if self.year else ""
    str_article += self.volume + '|' if self.volume else ""
    str_article += self.journal + '|' if self.journal else ""
    str_article += self.number + '|' if self.number else ""
    str_article += self.url + '|' if self.url else ""
    str_article += self.ee + '\n' if self.ee else ""


    return str_article

# process string to xml object, using BeautifulSoup
def process_buffer(buf):
  return BeautifulSoup(buf, "xml")

# read xml line by line to avoid crashing RAM
def input_xml(inputxml, maxentry):
  inputbuffer = ''
  articles = []
  with open(inputxml, 'rb') as inputfile:
    append = False
    for line in inputfile:
      if '<article' in line:
        inputbuffer = line
        append = True
      elif '</article>' in line:
        inputbuffer += line
        append = False

        articles.append(process_buffer(inputbuffer))
        if len(articles) == maxentry:
          break
  
        inputbuffer = None
        del inputbuffer
      elif append:
        inputbuffer += line
  return articles

# write xml to another file
def output_xml(output, articles):
  f = open(output,'w')
  f.write('id|author|title|pages|year|volume|journal|number|url|ee\n')
  count = 0
  for article in articles:
    count +=1
    tmp = Article(article)
    article_str = tmp.write_article(count)
    f.write(article_str)
    
  f.close()

articles = input_xml('dblp.xml',10000)
output_xml('dblp_proccessed.csv', articles)
