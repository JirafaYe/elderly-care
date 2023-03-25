default:
        docker build -t elderly-care .
        docker run --name elderly-care -p 8638:8638 -d elderly-care

clean:
        docker stop elderly-care
        docker rm elderly-care
        docker rmi elderly-care
