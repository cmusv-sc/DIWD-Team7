__author__ = 'Bailiang_Baby'
from py2neo import authenticate, Graph
from py2neo import Node, Relationship
# set up authentication parameters
authenticate("localhost:7474", "neo4j", "neo")

# connect to authenticated graph database
graph = Graph("http://localhost:7474/db/data/")


counter = 1
for line in open('first5000.csv'):
    if (counter == 1):
        counter = 2
        continue
    else:
        elements = line.split(",")
    # mdate,key,author,editor,title,pages,year,volume,journal,number,url,ee,address,"
	 #  		+ "booktitle,month,cdrom,publisher,note,crossref,isbn,,series,school,,chapter,cite

        mdate = elements[0]
        key = elements[1]
        author = elements[2]
        editor = elements[3]
        title = elements[4]
        pages = elements[5]
        year = elements[6]
        volume = elements[7]
        journal = elements[8]
        number = elements[9]
        url = elements[10]
        ee = elements[11]

        address = elements[12]
        booktitle = elements[13]
        month = elements[14]
        cdrom = elements[15]
        publisher = elements[16]
        note = elements[17]
        crossref = elements[18]
        isbn = elements[19]
        series = elements[20]
        school = elements[21]
        chapter = elements[22]
        cite = elements[23]

        oldPaper = graph.find_one("Paper", "mdate", mdate)

        paper = Node("Paper", title=title)
        print "paper created", counter
        counter = counter + 1

        paper.properties["mdate"] = mdate
        paper.properties["key"] = key
        paper.properties["editor"] = editor
        paper.properties["pages"] = pages
        paper.properties["year"] = int(year)
        paper.properties["volume"] = volume
        paper.properties["journal"] = journal
        paper.properties["number"] = number
        paper.properties["url"] = url
        paper.properties["ee"] = ee

        paper.properties["address"] = address
        paper.properties["booktitle"] = booktitle
        paper.properties["month"] = month
        paper.properties["cdrom"] = cdrom
        paper.properties["publisher"] = publisher
        paper.properties["note"] = note
        paper.properties["crossref"] = crossref
        paper.properties["isbn"] = isbn
        paper.properties["series"] = series
        paper.properties["school"] = school
        paper.properties["cite"] = cite
        paper.properties["chapter"] = chapter

        authors = author.split("|")



        if(oldPaper is None):
            print "No paper found !"
        else:
            print "Building citation relationship"
            paper_cite_paper = Relationship(paper, "CITE", oldPaper)
            graph.create(paper_cite_paper)

        # Current paper only has ONE author
        if(len(authors) == 1):

            print "Just 1 author  "
            oldAuthor = graph.find_one("Author", "name", authors[0])

            if (oldAuthor) is None:
                print "Creating new author"
                oldAuthor = Node("Author", name=authors[0])
            else:
                print "Find the existed author"

            author_publish_paper = Relationship(oldAuthor, "PUBLISH", paper)
            graph.create(author_publish_paper)
        # Current paper has multiple authors
        else:
            for i in range(len(authors)): # current paper author list
                j = i + 1

                fromAuthor = graph.find_one("Author", "name", authors[i])
                if fromAuthor is None:
                        fromAuthor = Node("Author", name=authors[i])
                author_publish_paper = Relationship(fromAuthor, "PUBLISH", paper)
                graph.create(author_publish_paper)

                while (j < len(authors)):

                    toAuthor = graph.find_one("Author", "name", authors[j])
                    if toAuthor is None:
                        toAuthor = Node("Author", name=authors[j])

                    j = j + 1

                    graph.create(toAuthor)
                    ifExist = graph.match_one(fromAuthor, "CO", toAuthor)

                    if(ifExist is None):
                        fromAuthor_To_toAuthor = Relationship(fromAuthor, "CO", toAuthor)
                        toAuthor_To_fromAuthor = Relationship(toAuthor, "CO", fromAuthor)
                        graph.create(fromAuthor_To_toAuthor)
                        graph.create(toAuthor_To_fromAuthor)
                    else:
                        continue




