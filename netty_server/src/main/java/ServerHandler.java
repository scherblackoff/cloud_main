import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ServerHandler extends ChannelInboundHandlerAdapter {


    private static final DatabaseHandler dbHandler = new DatabaseHandler();
    private static final String path = "netty_server/src/main/resources/server_dir/";

    private static final Logger logger = Logger.getLogger(ServerHandler.class.getName());


    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logger.log(Level.WARN, "Канал закрыт");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FileToDownload) {
            FileToDownload file = (FileToDownload) msg;
            logger.log(Level.INFO, String.format("Клиент %s отправил запрос на загрузку файла %s",
                    file.getNameUser(), file.getNameFile()));
            FileToUpload fileForClient = new FileToUpload(Paths.get(
                    path + file.getNameFile()));
            ctx.writeAndFlush(fileForClient);
            logger.log(Level.INFO, String.format("Файл %s отправлен клиенту %s",
                    file.getNameFile(), file.getNameUser()));
        }
        if (msg instanceof FileToUpload) {
            FileToUpload file = (FileToUpload) msg;
            logger.log(Level.INFO, String.format("Клиент %s отправил файл %s для сохранения",
                    file.getUserLogin(), file.getFileName()));
            dbHandler.uploadFileDb(file.getFileName(), file.getUserLogin());
            Files.write(Paths.get(path + file.getFileName()),
                    file.getFileData(), StandardOpenOption.CREATE);
            logger.log(Level.INFO, String.format("Файл %s полученный от клиента %s сохранен",
                    file.getFileName(), file.getUserLogin()));
        }
        if (msg instanceof User) {
            User user = (User) msg;
            if (user.getStatus().equals(User.Status.SIGN_UP)) {
                logger.log(Level.INFO, String.format("Регистрация клиента c login:[%s]...",
                        user.getLogin()));
                if (!dbHandler.clientIsExist(user.getLogin())) {
                    dbHandler.signUpUser(user);
                    logger.log(Level.WARN, String.format("Регистрация клиента c login[%s] прошла успешно",
                            user.getLogin()));
                } else {
                    user.setStatus(User.Status.USER_IS_REGISTERED);
                    logger.log(Level.ERROR, String.format("Клиент с login:[%s] уже зарегестрирован",
                            user.getLogin()));
                }
                ctx.writeAndFlush(user);
            }
            if (user.getStatus().equals(User.Status.SIGN_IN)) {
                logger.log(Level.WARN, String.format("Попытка входа клинта c login:[%s]",
                        user.getLogin()));
                ResultSet resultSet = dbHandler.getUser(user);
                int counter = 0;
                try {
                    while (resultSet.next()) {
                        counter++;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if(counter >= 1){
                    user.setStatus(User.Status.USER_IS_REGISTERED);
                    logger.log(Level.WARN, String.format("Вход клиента с login:[%s] произведен успешно", user.getLogin()));
                }
                ctx.writeAndFlush(user);
            }
        }
        if(msg instanceof ServerFiles){
            logger.log(Level.INFO, "Запрос на получение списка файлов, сохраненных на сервере");
            ServerFiles serverFiles = (ServerFiles) msg;
            serverFiles.setServerFiles(dbHandler.getServerFiles(serverFiles.getLogin()));
            ctx.writeAndFlush(serverFiles);
            logger.log(Level.INFO, "Список файлов, сохренных на сервере, отправлен");
        }
        if(msg instanceof FileToDelete){
            FileToDelete fileToDelete = (FileToDelete) msg;
            logger.log(Level.WARN, String.format("Запрос клиента с login:[%s] на удаление файла %s",
                    fileToDelete.getUserName(), fileToDelete.getNameFile()));
            dbHandler.deleteFile(fileToDelete);
            Files.delete(Paths.get(path + fileToDelete.getNameFile()));
            logger.log(Level.WARN, String.format("Файл %s, загруженный клиентом с login:[%s], успешно удален",
                    fileToDelete.getNameFile(), fileToDelete.getUserName()));
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.log(Level.WARN, "Клиент подключился");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.log(Level.WARN, "Клиент отключился");
        ctx.close();
    }
}
